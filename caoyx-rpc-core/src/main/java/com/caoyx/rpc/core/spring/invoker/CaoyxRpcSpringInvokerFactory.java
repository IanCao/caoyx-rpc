package com.caoyx.rpc.core.spring.invoker;

import com.caoyx.rpc.core.reference.CaoyxRpcReferenceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.lang.reflect.Field;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:20
 */
public class CaoyxRpcSpringInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(CaoyxRpcReference.class)) {
                    CaoyxRpcReference caoyxRpcReference = field.getAnnotation(CaoyxRpcReference.class);
                    String[] address = caoyxRpcReference.addressList();
                    CaoyxRpcReferenceBean referenceBean = new CaoyxRpcReferenceBean(address[0].split(":")[0], Integer.valueOf(address[0].split(":")[1]), field.getType(), caoyxRpcReference.version());
                    referenceBean.setClient(caoyxRpcReference.client());
                    referenceBean.setSerializer(caoyxRpcReference.serializer());
                    try {
                        referenceBean.init();
                        Object proxy = referenceBean.getObject();
                        field.setAccessible(true);
                        field.set(bean, proxy);
                    } catch (InstantiationException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return super.postProcessAfterInitialization(bean, beanName);
    }
}