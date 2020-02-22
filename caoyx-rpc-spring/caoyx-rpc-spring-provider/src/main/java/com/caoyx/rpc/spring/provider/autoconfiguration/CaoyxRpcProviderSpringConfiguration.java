package com.caoyx.rpc.spring.provider.autoconfiguration;

import com.caoyx.rpc.core.config.CaoyxRpcProviderConfig;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.net.netty.server.NettyServer;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.register.RegisterType;
import com.caoyx.rpc.core.utils.StringUtils;
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

    @Value("${caoyxRpc.provider.applicationName}")
    private String applicationName;

    @Value("${caoyxRpc.provider.register.type:}")
    private String register;

    @Value("${caoyxRpc.provider.register.address:}")
    private String address;

    @Value("${caoyxRpc.provider.accessToken:}")
    private String accessToken;

    @ConditionalOnMissingBean(CaoyxRpcSpringProviderFactory.class)
    @Bean
    public CaoyxRpcSpringProviderFactory caoyxRpcSpringProviderFactory() throws CaoyxRpcException {
        log.info("caoyxRpc-spring-provider starting.....");

        CaoyxRpcProviderConfig config = new CaoyxRpcProviderConfig();
        config.setApplicationName(applicationName);
        config.setAccessToken(accessToken);
        config.setPort(port);

        RegisterType registerType = RegisterType.findByValue(register);
        if (StringUtils.isNotBlank(address) && registerType != null) {
            config.setRegisterConfig(new RegisterConfig(address, registerType));
        }
        return new CaoyxRpcSpringProviderFactory(config);
    }
}