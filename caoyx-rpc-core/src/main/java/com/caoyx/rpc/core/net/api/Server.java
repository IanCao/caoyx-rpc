package com.caoyx.rpc.core.net.api;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;

/**
 * @author caoyixiong
 */
public interface Server {

    void start(int port, CaoyxRpcProviderFactory caoyxRpcProviderFactory) throws CaoyxRpcException;

    void stop();
}