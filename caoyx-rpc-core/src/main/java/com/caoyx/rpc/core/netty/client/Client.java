package com.caoyx.rpc.core.netty.client;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.reference.CaoyxRpcReferenceBean;

/**
 * @author caoyixiong
 */
public interface Client {

    void init(CaoyxRpcReferenceBean caoyxRpcReferenceBean) throws InterruptedException;

    void stop();

    void doSend(CaoyxRpcRequest requestPacket) throws InterruptedException;
}