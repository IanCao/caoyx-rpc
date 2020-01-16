package com.caoyx.rpc.spring.provider;

import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.net.api.Server;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import com.caoyx.rpc.core.register.RegisterConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 20:29
 */

public class CaoyxRpcSpringProviderFactory extends CaoyxRpcProviderFactory implements ApplicationContextAware {

    public CaoyxRpcSpringProviderFactory(String applicationName, Server server, RegisterConfig registerConfig, String version, List<CaoyxRpcFilter> rpcFilters) {
        super(applicationName, server, registerConfig, version, rpcFilters);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(CaoyxRpcService.class);
        if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                CaoyxRpcService caoyxRpcService = serviceBean.getClass().getAnnotation(CaoyxRpcService.class);
                String iFace = serviceBean.getClass().getInterfaces()[0].getName();
                String implVersion = caoyxRpcService.implVersion();
                addServiceBean(iFace, implVersion, serviceBean);
            }
        }
    }
}