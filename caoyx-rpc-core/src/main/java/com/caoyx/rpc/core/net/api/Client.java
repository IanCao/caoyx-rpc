package com.caoyx.rpc.core.net.api;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.data.Address;

/**
 * @author caoyixiong
 */
public interface Client {

    void init(Address address, CaoyxRpcInvokerFactory invokerFactory) throws InterruptedException;

    void close();

    void doSend(CaoyxRpcRequest requestPacket);

    boolean isValid();
}