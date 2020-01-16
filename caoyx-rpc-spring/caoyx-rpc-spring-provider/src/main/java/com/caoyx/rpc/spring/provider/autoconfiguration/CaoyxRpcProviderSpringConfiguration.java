package com.caoyx.rpc.spring.provider.autoconfiguration;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.net.netty.server.NettyServer;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.spring.provider.CaoyxRpcSpringProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-26 14:33
 */
@Configuration
public class CaoyxRpcProviderSpringConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CaoyxRpcProviderSpringConfiguration.class);

    @Value("${caoyxRpc.port:1118}")
    private int port;

    @Value("${caoyxRpc.applicationName}")
    private String applicationName;

    @Value("${caoyxRpc.register.type:noRegister}")
    private String registerType;

    @Value("${caoyxRpc.register.address:}")
    private String registerAddress;

    @Value("${caoyxRpc.applicationVersion:0}")
    private String applicationVersion;

    @ConditionalOnMissingBean(CaoyxRpcSpringProviderFactory.class)
    @Bean
    public CaoyxRpcSpringProviderFactory caoyxRpcSpringProviderFactory() throws InterruptedException, CaoyxRpcException {
        log.info("caoyxRpcSpringProviderFactory init");
        CaoyxRpcSpringProviderFactory factory = new CaoyxRpcSpringProviderFactory(applicationName,
                new NettyServer(),
                new RegisterConfig(registerType, registerAddress),
                applicationVersion,
                null);
        factory.setPort(port);
        factory.init();
        return factory;
    }
}