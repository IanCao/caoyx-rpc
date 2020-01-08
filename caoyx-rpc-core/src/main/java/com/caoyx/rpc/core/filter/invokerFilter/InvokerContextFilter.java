package com.caoyx.rpc.core.filter.invokerFilter;

import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 16:23
 */
public class InvokerContextFilter implements CaoyxRpcFilter {

    @Override
    public void invokeRequestHandler(CaoyxRpcRequest rpcRequest) {
    }

    @Override
    public void invokeResponseHandler(CaoyxRpcResponse response) {
        CaoyxRpcContext.removeContext();
    }

    @Override
    public void doProcess(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) {

    }
}