package com.caoyx.rpc.core.filter.invokerFilter;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 17:06
 */
@Slf4j
public class InvokerRetryFilter implements CaoyxRpcFilter {

    @Setter
    private int retryTimes;

    private LoadBalanceInvokerFilter loadBalanceInvokerFilter;

    public InvokerRetryFilter(LoadBalanceInvokerFilter loadBalanceInvokerFilter, int retryTimes) {
        this.loadBalanceInvokerFilter = loadBalanceInvokerFilter;
        this.retryTimes = retryTimes;
    }

    @Override
    public void invokeRequestHandler(CaoyxRpcRequest rpcRequest) {

    }

    @Override
    public void invokeResponseHandler(CaoyxRpcResponse rpcResponse) {
    }

    @Override
    public void doProcess(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) throws Exception {
        if (rpcResponse != null && rpcResponse.getStatus() == CaoyxRpcStatus.SUCCESS) {
            return;
        }
        for (int i = 0; i < retryTimes; i++) {
            log.error("remoteInvoker:[" + rpcRequest.getInvokerInfo() + "]" + "caoyxRpc RetryTimes is" + i);
            loadBalanceInvokerFilter.doProcess(rpcRequest, rpcResponse);
        }
    }
}