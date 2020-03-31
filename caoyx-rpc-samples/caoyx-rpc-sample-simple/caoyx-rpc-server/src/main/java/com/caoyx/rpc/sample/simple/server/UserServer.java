package com.caoyx.rpc.sample.simple.server;

import com.caoyx.rpc.core.config.CaoyxRpcProviderConfig;
import com.caoyx.rpc.core.register.RegisterType;
import com.caoyx.rpc.sample.simple.api.IUser;
import com.caoyx.rpc.sample.simple.server.impl.UserImpl;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import com.caoyx.rpc.core.register.RegisterConfig;

/**
 * @author caoyixiong
 */
public class UserServer {
    public static void main(String[] args) throws CaoyxRpcException {
        CaoyxRpcProviderConfig config = new CaoyxRpcProviderConfig();
        config.setApplicationName("caoyxRpc-sample-simple-server");
        config.setPort(1118);
        config.setRateLimit(1);
        config.setAccessToken("caoyx");
        config.setRegisterConfig(new RegisterConfig("127.0.0.1:8848",RegisterType.NACOS));
        CaoyxRpcProviderFactory caoyxRpcProviderFactory = new CaoyxRpcProviderFactory(config);
        caoyxRpcProviderFactory.exportService(IUser.class, new UserImpl());
    }
}