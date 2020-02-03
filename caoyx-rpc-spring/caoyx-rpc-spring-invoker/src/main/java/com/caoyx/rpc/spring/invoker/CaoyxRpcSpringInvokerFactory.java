package com.caoyx.rpc.spring.invoker;

import com.caoyx.rpc.core.config.CaoyxRpcInvokerConfig;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.caoyx.rpc.core.invoker.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.register.RegisterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
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

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) {
                if (field.isAnnotationPresent(CaoyxRpcReference.class)) {
                    CaoyxRpcReference caoyxRpcReference = field.getAnnotation(CaoyxRpcReference.class);
                    if (caoyxRpcReference.loadAddress().length == 0
                            && !StringUtils.hasText(caoyxRpcReference.registerAddress())) {
                        CaoyxRpcException exception = new CaoyxRpcException("load address and register address are all null");
                        log.error(exception.getMessage(), exception);
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

                    CaoyxRpcInvokerConfig config = new CaoyxRpcInvokerConfig();
                    config.setIFace(field.getType());
                    config.setRemoteApplicationName(caoyxRpcReference.remoteApplicationName());
                    config.setRegisterConfig(new RegisterConfig(
                            caoyxRpcReference.register().getValue(), caoyxRpcReference.registerAddress(), Arrays.asList(caoyxRpcReference.loadAddress())));
                    config.setSerializerType(caoyxRpcReference.serializer());
                    config.setLoadBalanceType(caoyxRpcReference.loadBalance());
                    config.setRpcFilters(caoyxRpcFilters);
                    config.setRetryTimes(caoyxRpcReference.retryTimes());
                    config.setTimeout(caoyxRpcReference.timeout());
                    config.setCallType(caoyxRpcReference.callType());

                    if (StringUtils.hasText(caoyxRpcReference.failBack())) {
                        Object failBack = applicationContext.getBean(caoyxRpcReference.failBack());
                        if (failBack == null) {
                            log.warn("CaoyxRpcInvokerFailBack-BeanName-[" + caoyxRpcReference.failBack() + "] is not exist");
                        } else {
                            if (failBack instanceof CaoyxRpcInvokerFailBack) {
                                config.setCaoyxRpcInvokerFailBack((CaoyxRpcInvokerFailBack) failBack);
                            } else {
                                log.warn("CaoyxRpcInvokerFailBack-BeanName-[" + caoyxRpcReference.failBack() + "] is not an instance of CaoyxRpcInvokerFailBack");
                            }
                        }
                    }
                    config.setRemoteApplicationVersion(caoyxRpcReference.remoteapplicationVersion());
                    config.setRemoteImplVersion(caoyxRpcReference.remoteImplVersion());

                    try {
                        CaoyxRpcReferenceBean referenceBean = new CaoyxRpcReferenceBean(config);
                        referenceBean.init();
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