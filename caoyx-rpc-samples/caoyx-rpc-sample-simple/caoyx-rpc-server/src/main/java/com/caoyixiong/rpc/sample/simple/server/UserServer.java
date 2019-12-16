package com.caoyixiong.rpc.sample.simple.server;

import com.caoyixiong.rpc.sample.simple.api.IUser;
import com.caoyixiong.rpc.sample.simple.server.impl.UserImpl;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;

/**
 * @author caoyixiong
 */
public class UserServer {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InterruptedException {
        CaoyxRpcProviderFactory caoyxRpcProviderFactory = new CaoyxRpcProviderFactory();
        caoyxRpcProviderFactory.addServiceBean(IUser.class.getName(), 1, new UserImpl());
        caoyxRpcProviderFactory.init();
    }
}