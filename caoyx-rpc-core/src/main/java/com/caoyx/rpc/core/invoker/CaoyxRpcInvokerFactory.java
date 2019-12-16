package com.caoyx.rpc.core.invoker;

import com.caoyx.rpc.core.data.CaoyxRpcResponse;

import lombok.Getter;
import lombok.Setter;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caoyixiong
 */
public class CaoyxRpcInvokerFactory {

    private static volatile CaoyxRpcInvokerFactory instance = new CaoyxRpcInvokerFactory();

    public static CaoyxRpcInvokerFactory getInstance() {
        return instance;
    }

    @Getter
    @Setter
    private SocketAddress remoteAddress;

    private ConcurrentHashMap<String, CaoyxRpcFutureResponse> pendingRpc = new ConcurrentHashMap<>();

    public void setInvokerFuture(String requestId, CaoyxRpcFutureResponse futureResponse) {
        pendingRpc.put(requestId, futureResponse);
    }

    public void removeInvokerFuture(String requestId) {
        pendingRpc.remove(requestId);
    }

    public void setResponse(CaoyxRpcResponse caoyxRpcResponse) {
        CaoyxRpcFutureResponse futureResponse = pendingRpc.get(caoyxRpcResponse.getRequestId());
        futureResponse.setResponse(caoyxRpcResponse);
        removeInvokerFuture(caoyxRpcResponse.getRequestId());
    }
}