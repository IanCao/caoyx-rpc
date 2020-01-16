package com.caoyx.rpc.core.filter.invokerFilter;

import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 17:18
 */
@Slf4j
public class LoadBalanceInvokerFilter implements CaoyxRpcFilter {

    private LoadBalance loadBalance;
    private CaoyxRpcRegister register;
    private RemoteInvokerFilter remoteInvokerFilter;

    public LoadBalanceInvokerFilter(RemoteInvokerFilter remoteInvokerFilter, LoadBalance loadBalance, CaoyxRpcRegister register) {
        this.remoteInvokerFilter = remoteInvokerFilter;
        this.loadBalance = loadBalance;
        this.register = register;
    }

    @Override
    public void invokeRequestHandler(CaoyxRpcRequest rpcRequest) {

    }

    @Override
    public void invokeResponseHandler(CaoyxRpcResponse rpcResponse) {

    }

    @Override
    public void doProcess(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) throws Exception {
        Address targetAddress = loadBalance.loadBalance(new ArrayList<>(register.getAllRegister(rpcRequest.getApplicationName(), rpcRequest.getApplicationVersion())));
        if (targetAddress == null) {
            throw new CaoyxRpcException("LoadBalanceFilter - targetAddress is null");
        }
        CaoyxRpcContext.getContext().setRemoteAddress(targetAddress);
        log.info("[" + rpcRequest.getInvokerInfo() + "]'s loadBalance is " + targetAddress.toString());
        remoteInvokerFilter.doProcess(rpcRequest, rpcResponse);
    }
}