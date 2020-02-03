package com.caoyx.rpc.core.invoker;

import com.caoyx.rpc.core.data.CaoyxRpcResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caoyixiong
 */
@Slf4j
public enum CaoyxRpcPendingInvokerPool {
    INSTANCE;

    private ConcurrentHashMap<String, CaoyxRpcFutureResponse> pendingRpc = new ConcurrentHashMap<>();

    public void setPendingInvoke(String requestId, CaoyxRpcFutureResponse futureResponse) {
        pendingRpc.put(requestId, futureResponse);
    }

    public CaoyxRpcFutureResponse removeInvokerFuture(String requestId) {
        return pendingRpc.remove(requestId);
    }

    public void notifyResponse(CaoyxRpcResponse caoyxRpcResponse) {
        CaoyxRpcFutureResponse futureResponse = removeInvokerFuture(caoyxRpcResponse.getRequestId());
        if (futureResponse == null) {
            return;
        }
        futureResponse.notifyResponse(caoyxRpcResponse);
    }
}