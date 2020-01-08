package com.caoyx.rpc.core.filter.invokerFilter;

import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.invoker.CaoyxRpcFutureResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.netty.client.Client;
import com.caoyx.rpc.core.netty.client.ClientManager;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 16:56
 */
@Slf4j
public class RemoteInvokerFilter implements CaoyxRpcFilter {

    private ClientManager clientManager;
    private Class<? extends Client> client;
    private CaoyxRpcInvokerFactory invokerFactory;


    public RemoteInvokerFilter(ClientManager clientManager, Class<? extends Client> client, CaoyxRpcInvokerFactory invokerFactory) {
        this.clientManager = clientManager;
        this.client = client;
        this.invokerFactory = invokerFactory;
    }

    @Override
    public void invokeRequestHandler(CaoyxRpcRequest rpcRequest) {

    }

    @Override
    public void invokeResponseHandler(CaoyxRpcResponse response) {

    }

    @Override
    public void doProcess(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) throws Exception {
        Address remoteAddress = CaoyxRpcContext.getContext().getRemoteAddress();
        if (remoteAddress == null) {
            throw new CaoyxRpcException("RemoteInvokerFilter - targetAddress is not set");
        }
        Client clientInstance = clientManager.getOrCreateClient(remoteAddress, client, invokerFactory);
        clientInstance.doSend(rpcRequest);

        CaoyxRpcFutureResponse futureResponse = new CaoyxRpcFutureResponse(invokerFactory, rpcRequest);
        CaoyxRpcResponse caoyxRpcResponse = futureResponse.get(rpcRequest.getTimeout(), TimeUnit.MILLISECONDS);

        rpcResponse.setStatus(caoyxRpcResponse.getStatus());
        rpcResponse.setErrorMsg(caoyxRpcResponse.getErrorMsg());
        rpcResponse.setResult(caoyxRpcResponse.getResult());

        if (!rpcResponse.isSuccess()) {
            log.error(rpcResponse.getErrorMsg());
        }
    }
}