package com.caoyx.rpc.benchmark.caoyxRpc;

import com.caoyx.rpc.benchmark.base.UserService;
import com.caoyx.rpc.benchmark.base.UserServiceImpl;
import com.caoyx.rpc.core.config.CaoyxRpcProviderConfig;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.register.RegisterType;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:37
 */
public class Server {
    public static void main(String[] args) {

        CaoyxRpcProviderConfig providerConfig = new CaoyxRpcProviderConfig();
        providerConfig.setApplicationName("caoyxRpc-benchmark-server");
        providerConfig.setPort(1118);
        providerConfig.setRegisterConfig(new RegisterConfig("127.0.0.1", RegisterType.DIRECT));

        CaoyxRpcProviderFactory caoyxRpcProviderFactory = new CaoyxRpcProviderFactory(providerConfig);
        caoyxRpcProviderFactory.exportService(UserService.class.getName(), new UserServiceImpl());
    }
}