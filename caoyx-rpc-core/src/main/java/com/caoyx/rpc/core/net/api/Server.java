package com.caoyx.rpc.core.net.api;

import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;

/**
 * @author caoyixiong
 */
public interface Server {

    void start(CaoyxRpcProviderFactory caoyxRpcProviderFactory) throws InterruptedException;

    void stop();
}