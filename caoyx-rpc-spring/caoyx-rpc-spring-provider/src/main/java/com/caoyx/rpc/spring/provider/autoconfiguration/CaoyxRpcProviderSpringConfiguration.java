package com.caoyx.rpc.spring.provider.autoconfiguration;

import com.caoyx.rpc.core.netty.server.NettyServer;
import com.caoyx.rpc.core.register.impl.ZookeeperRegister;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;
import com.caoyx.rpc.spring.provider.CaoyxRpcSpringProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-26 14:33
 */
@Slf4j
@Configuration
public class CaoyxRpcProviderSpringConfiguration {

    @Value("${caoyxRpc.port:1119}")
    private int port;

    @Value("${caoyxRpc.remote.applicationName}")
    private String remoteApplicationName;

    @Value("${caoyxRpc.register.zookeeper.address}")
    private String zooKeeperAddress;


    @ConditionalOnMissingBean(CaoyxRpcSpringProviderFactory.class)
    @Bean
    public CaoyxRpcSpringProviderFactory caoyxRpcSpringProviderFactory() throws IllegalAccessException, InterruptedException, InstantiationException {
        log.info("caoyxRpcSpringProviderFactory init");
        CaoyxRpcSpringProviderFactory factory = new CaoyxRpcSpringProviderFactory(remoteApplicationName,
                new NettyServer(),
                new JDKSerializerImpl(),
                new ZookeeperRegister(zooKeeperAddress),
                0);
        factory.setPort(port);  // todo 服务治理
        factory.init();
        return factory;
    }
}