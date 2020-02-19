package com.caoyx.rpc.spring.provider.autoconfiguration;

import com.caoyx.rpc.core.config.CaoyxRpcProviderConfig;
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

    @Value("${caoyxRpc.server.port:1118}")
    private int port;

    @Value("${caoyxRpc.server.applicationName}")
    private String applicationName;

    @Value("${caoyxRpc.server.register.type:noRegister}")
    private String registerType;

    @Value("${caoyxRpc.server.register.address:}")
    private String registerAddress;

    @Value("${caoyxRpc.server.applicationVersion:0}")
    private String applicationVersion;

    @Value("${caoyxRpc.server.accessToken:}")
    private String accessToken;

    @ConditionalOnMissingBean(CaoyxRpcSpringProviderFactory.class)
    @Bean
    public CaoyxRpcSpringProviderFactory caoyxRpcSpringProviderFactory() throws InterruptedException, CaoyxRpcException {
        log.info("caoyxRpcSpringProviderFactory init");
        CaoyxRpcProviderConfig config = new CaoyxRpcProviderConfig();
        config.setApplicationName(applicationName);
        config.setRegisterConfig(new RegisterConfig(registerType, registerAddress));
        config.setApplicationVersion(applicationVersion);
        config.setAccessToken(accessToken);
        config.setPort(port);

        CaoyxRpcSpringProviderFactory factory = new CaoyxRpcSpringProviderFactory(config);
        factory.init();
        return factory;
    }
}