package com.caoyx.rpc.core.net.api;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import com.caoyx.rpc.core.shutdown.GraceFullyShutDownCallBack;

/**
 * @author caoyixiong
 */
public interface Server extends GraceFullyShutDownCallBack {

    void start(int port, CaoyxRpcProviderFactory caoyxRpcProviderFactory) throws CaoyxRpcException;
}