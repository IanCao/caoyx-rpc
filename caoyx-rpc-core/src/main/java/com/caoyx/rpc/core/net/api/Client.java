package com.caoyx.rpc.core.net.api;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;

/**
 * @author caoyixiong
 */
public interface Client {

    void init(String ipPort) throws InterruptedException;

    void close();

    void doSend(CaoyxRpcRequest requestPacket);

    boolean isValid();
}