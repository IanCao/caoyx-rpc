package com.caoyx.rpc.spring.invoker;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.rebalance.impl.RandomRebalance;
import com.caoyx.rpc.core.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.register.impl.zookeeper.ZookeeperRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

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
                    if (!StringUtils.hasText(caoyxRpcReference.address())
                            && !StringUtils.hasText(caoyxRpcReference.registerAddress())) {
                        CaoyxRpcException exception = new CaoyxRpcException("remote address and register address are all null");
                        log.error(exception.getMessage(), exception);
                    }
                    Address address = null;
                    if (caoyxRpcReference.callType() == CallType.DIRECT) {
                        String[] ipPort = caoyxRpcReference.address().split(";");
                        address = new Address(ipPort[0], Integer.valueOf(ipPort[1]));
                    }
                    CaoyxRpcReferenceBean referenceBean = new CaoyxRpcReferenceBean(field.getType(),
                            caoyxRpcReference.version(),
                            caoyxRpcReference.remoteApplicationName(),
                            new RegisterConfig(new ZookeeperRegister(), caoyxRpcReference.registerAddress()),
                            caoyxRpcReference.client(),
                            caoyxRpcReference.serializer());

                    referenceBean.setClient(caoyxRpcReference.client());
                    referenceBean.setSerializerAlgorithm(caoyxRpcReference.serializer());
                    referenceBean.setCallType(caoyxRpcReference.callType());
                    referenceBean.setRebalance(new RandomRebalance());

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