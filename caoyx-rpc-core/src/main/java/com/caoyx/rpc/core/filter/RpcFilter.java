package com.caoyx.rpc.core.filter;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;


/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 16:32
 */
public interface RpcFilter {
    void invokeRequestHandler(CaoyxRpcRequest rpcRequest);

    void invokeResponseHandler(CaoyxRpcResponse rpcResponse);

    void doProcess(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) throws Exception;
}