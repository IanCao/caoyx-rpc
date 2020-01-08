package com.caoyx.rpc.core.filter.providerFilter;

import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 20:17
 */
public class ProviderContenxtFilter implements CaoyxRpcFilter {
    @Override
    public void invokeRequestHandler(CaoyxRpcRequest rpcRequest) {
        CaoyxRpcContext caoyxRpcContext = CaoyxRpcContext.getContext();
        caoyxRpcContext.setRemoteAddress(rpcRequest.getAddress());
        caoyxRpcContext.setMetaData(rpcRequest.getMetaData());
    }

    @Override
    public void invokeResponseHandler(CaoyxRpcResponse rpcResponse) {
        CaoyxRpcContext.removeContext();
    }

    @Override
    public void doProcess(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) throws Exception {

    }
}