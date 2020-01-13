package com.caoyx.rpc.spring.invoker;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
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
                    CaoyxRpcReferenceBean referenceBean = null;
                    try {
                        referenceBean = new CaoyxRpcReferenceBean(field.getType(),
                                caoyxRpcReference.version(),
                                caoyxRpcReference.remoteApplicationName(),
                                new RegisterConfig(
                                        caoyxRpcReference.register().getValue(), caoyxRpcReference.registerAddress(), Arrays.asList(caoyxRpcReference.loadAddress())),
                                caoyxRpcReference.client(),
                                caoyxRpcReference.serializer(),
                                caoyxRpcReference.loadBalance(),
                                caoyxRpcFilters);
                    } catch (CaoyxRpcException e) {
                        log.error(field.getType() + " init fail");
                    }

                    referenceBean.setRetryTimes(caoyxRpcReference.retryTimes());
                    referenceBean.setTimeout(caoyxRpcReference.timeout());
                    referenceBean.setCallType(caoyxRpcReference.callType());

                    try {
                        referenceBean.init();
                        Object proxy = referenceBean.getObject();
                        field.setAccessible(true);
                        field.set(bean, proxy);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
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