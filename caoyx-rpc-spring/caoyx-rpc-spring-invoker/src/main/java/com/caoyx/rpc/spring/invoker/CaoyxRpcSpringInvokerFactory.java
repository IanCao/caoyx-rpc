package com.caoyx.rpc.spring.invoker;

import com.caoyx.rpc.core.config.CaoyxRpcInvokerConfig;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.loadbalance.LoadBalanceType;
import com.caoyx.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.caoyx.rpc.core.invoker.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.register.RegisterType;
import com.caoyx.rpc.core.serialization.SerializerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:20
 */
@Slf4j
public class CaoyxRpcSpringInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    @Value("${caoyxRpc.invoker.applicationName}")
    private String applicationName;

    @Value("${caoyxRpc.invoker.register.type:direct}")
    private String registerType;

    @Value("${caoyxRpc.invoker.register.address:}")
    private String address;

    @Value("${caoyxRpc.invoker.accessToken:}")
    private String accessToken;

    @Value("${caoyxRpc.invoker.timeout:3000}")
    private long timeout;

    @Value("${caoyxRpc.invoker.loadBalance:random}")
    private String loadBalance;

    @Value("${caoyxRpc.invoker.serializer:protoStuff}")
    private String serializer;

    @Value("${caoyxRpc.invoker.retry:0}")
    private int retry;

    @Value("${caoyxRpc.invoker.callType:sync}")
    private String callType;

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) {
                if (field.isAnnotationPresent(CaoyxRpcReference.class)) {
                    if (StringUtils.isEmpty(applicationName)) {
                        throw new CaoyxRpcException("applicationName can't be null");
                    }
                    CaoyxRpcReference caoyxRpcReference = field.getAnnotation(CaoyxRpcReference.class);
                    if (StringUtils.isEmpty(registerType) && StringUtils.isEmpty(caoyxRpcReference.registerType())) {
                        throw new CaoyxRpcException("registerType can't be null");
                    }
                    if (StringUtils.isEmpty(address) && StringUtils.isEmpty(caoyxRpcReference.address())) {
                        throw new CaoyxRpcException("address can't be null");
                    }
                    String[] filterBeanNames = caoyxRpcReference.filters();
                    List<CaoyxRpcFilter> caoyxRpcFilters = new ArrayList<>();
                    if (filterBeanNames.length > 0) {
                        for (int i = 0; i < filterBeanNames.length; i++) {
                            Object filterBean = applicationContext.getBean(filterBeanNames[i]);
                            if (!(filterBean instanceof CaoyxRpcFilter)) {
                                log.warn("beanName:[" + filterBeanNames[i] + "] is not an instance of CaoyxRpcFilter");
                                continue;
                            }
                            caoyxRpcFilters.add((CaoyxRpcFilter) filterBean);
                        }
                    }

                    RegisterType type = StringUtils.isEmpty(caoyxRpcReference.registerType())
                            ? RegisterType.findByValue(registerType)
                            : RegisterType.findByValue(caoyxRpcReference.registerType());
                    if (type == null) {
                        throw new CaoyxRpcException("registerType is invalid");
                    }
                    SerializerType serializerType = StringUtils.isEmpty(caoyxRpcReference.serializer())
                            ? SerializerType.findByLabel(serializer)
                            : SerializerType.findByLabel(caoyxRpcReference.serializer());
                    if (serializerType == null) {
                        throw new CaoyxRpcException("serializer is invalid");
                    }
                    LoadBalanceType loadBalanceType = StringUtils.isEmpty(caoyxRpcReference.loadBalance())
                            ? LoadBalanceType.findByValue(loadBalance)
                            : LoadBalanceType.findByValue(caoyxRpcReference.loadBalance());
                    if (loadBalanceType == null) {
                        throw new CaoyxRpcException("loadBalance is invalid");
                    }
                    CallType callType = StringUtils.isEmpty(caoyxRpcReference.callType())
                            ? CallType.findByValue(CaoyxRpcSpringInvokerFactory.this.callType)
                            : CallType.findByValue(caoyxRpcReference.callType());
                    if (callType == null) {
                        throw new CaoyxRpcException("callType is invalid");
                    }

                    CaoyxRpcInvokerConfig config = new CaoyxRpcInvokerConfig();
                    config.setApplicationName(applicationName);
                    config.setIFace(field.getType());
                    config.setProviderApplicationName(caoyxRpcReference.providerApplicationName());
                    config.setRegisterConfig(new RegisterConfig(StringUtils.isEmpty(caoyxRpcReference.address()) ? address : caoyxRpcReference.address(), type));
                    config.setSerializerType(serializerType);
                    config.setLoadBalanceType(loadBalanceType);
                    config.setRpcFilters(caoyxRpcFilters);
                    config.setRetryTimes(caoyxRpcReference.retry() <= 0 ? retry : caoyxRpcReference.retry());
                    config.setTimeout(caoyxRpcReference.timeout() <= 0 ? timeout : caoyxRpcReference.timeout());
                    config.setCallType(callType);
                    config.setAccessToken(StringUtils.isEmpty(caoyxRpcReference.accessToken()) ? accessToken : caoyxRpcReference.accessToken());

                    if (StringUtils.hasText(caoyxRpcReference.failCallBack())) {
                        Object failBack = applicationContext.getBean(caoyxRpcReference.failCallBack());
                        if (failBack == null) {
                            log.warn("CaoyxRpcInvokerFailBack-BeanName-[" + caoyxRpcReference.failCallBack() + "] is not exist");
                        } else {
                            if (failBack instanceof CaoyxRpcInvokerFailBack) {
                                config.setCaoyxRpcInvokerFailBack((CaoyxRpcInvokerFailBack) failBack);
                            } else {
                                log.warn("CaoyxRpcInvokerFailBack-BeanName-[" + caoyxRpcReference.failCallBack() + "] is not an instance of CaoyxRpcInvokerFailBack");
                            }
                        }
                    }
                    config.setProviderImplVersion(caoyxRpcReference.providerImplVersion());

                    try {
                        CaoyxRpcReferenceBean referenceBean = new CaoyxRpcReferenceBean(config);
                        Object proxy = referenceBean.getObject();
                        field.setAccessible(true);
                        field.set(bean, proxy);
                    } catch (Exception e) {
                        log.error(field.getType() + " init fail", e);
                    }
                }
            }
        });
        return super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}