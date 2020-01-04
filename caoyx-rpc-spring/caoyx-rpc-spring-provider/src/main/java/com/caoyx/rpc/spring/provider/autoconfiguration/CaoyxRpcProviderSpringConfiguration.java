package com.caoyx.rpc.spring.provider.autoconfiguration;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.netty.server.NettyServer;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.register.zookeeper.ZookeeperRegister;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;
import com.caoyx.rpc.spring.provider.CaoyxRpcSpringProviderFactory;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${caoyxRpc.register.address}")
    private String registerAddress;


//    @ConditionalOnMissingBean(CaoyxRpcSpringProviderFactory.class)
    @Bean
    public CaoyxRpcSpringProviderFactory caoyxRpcSpringProviderFactory() throws InterruptedException, CaoyxRpcException {
        log.info("caoyxRpcSpringProviderFactory init");
        CaoyxRpcSpringProviderFactory factory = new CaoyxRpcSpringProviderFactory(applicationName,
                new NettyServer(),
                new JDKSerializerImpl(),
                new RegisterConfig("zookeeper", registerAddress, null),
                "0");
        factory.setPort(port);  // todo 服务治理
        factory.init();
        return factory;
    }
}