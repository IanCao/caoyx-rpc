package com.caoyx.rpc.spring.invoker;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.caoyx.rpc.core.invoker.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.register.RegisterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:20
 */
@Slf4j
public class CaoyxRpcSpringInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
                    CaoyxRpcReferenceBean referenceBean = new CaoyxRpcReferenceBean(field.getType(),
                            caoyxRpcReference.version(),
                            caoyxRpcReference.remoteApplicationName(),
                            new RegisterConfig(
                                    caoyxRpcReference.register(), caoyxRpcReference.registerAddress(), Arrays.asList(caoyxRpcReference.loadAddress())),
                            caoyxRpcReference.client(),
                            caoyxRpcReference.serializer()
                            , null);

                    referenceBean.setRetryTimes(caoyxRpcReference.retryTimes());
                    referenceBean.setTimeout(caoyxRpcReference.timeout());
                    referenceBean.setCallType(caoyxRpcReference.callType());
                    referenceBean.setLoadBalance(new RandomLoadBalance());

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
}