package com.caoyx.rpc.sample.simple.server;

import com.caoyx.rpc.sample.simple.api.IUser;
import com.caoyx.rpc.sample.simple.server.impl.UserImpl;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.net.netty.server.NettyServer;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import com.caoyx.rpc.core.register.RegisterConfig;

/**
 * @author caoyixiong
 */
public class UserServer {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InterruptedException, CaoyxRpcException {
        String applicationName = "caoyxRpc-sample-simple-server";
        String applicationVersion = "0";
        String implVersion = "0";
        CaoyxRpcProviderFactory caoyxRpcProviderFactory = new CaoyxRpcProviderFactory(applicationName,
                new NettyServer(),
                new RegisterConfig(
                        "noRegister",
                        "",
                        null
                ),
                applicationVersion
                , null);
        caoyxRpcProviderFactory.setPort(1118);
        caoyxRpcProviderFactory.addServiceBean(IUser.class.getName(), implVersion, new UserImpl());
        caoyxRpcProviderFactory.init();
    }
}