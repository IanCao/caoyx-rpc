package com.caoyx.rpc.core.invoker.reference;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.invoker.CaoyxRpcFuture;
import com.caoyx.rpc.core.invoker.CaoyxRpcFutureResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcPendingInvokerPool;
import com.caoyx.rpc.core.invoker.Invocation;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: caoyixiong
 * @Date: 2020-04-03 12:52
 */
@Slf4j
public class CaoyxRpcInvoker {
    public CaoyxRpcResponse doInvoke(CaoyxRpcRequest rpcRequest, Invocation invocation) {
        CaoyxRpcResponse rpcResponse = new CaoyxRpcResponse();

        for (int j = 0; j < invocation.getRpcFilters().size(); j++) {
            try {
                invocation.getRpcFilters().get(j).invokeRequestHandler(rpcRequest);
            } catch (Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
            }
        }

        CaoyxRpcFutureResponse futureResponse = new CaoyxRpcFutureResponse(rpcRequest);
        CaoyxRpcPendingInvokerPool.INSTANCE.setPendingInvoke(rpcRequest.getRequestId(), futureResponse);
        invocation.getClientInstance().doSend(rpcRequest);

        switch (invocation.getCallType()) {
            case SYNC:
                CaoyxRpcResponse caoyxRpcResponse = null;
                try {
                    caoyxRpcResponse = futureResponse.get(rpcRequest.getTimeout(), TimeUnit.MILLISECONDS);
                    rpcResponse.setStatus(caoyxRpcResponse.getStatus());
                    rpcResponse.setErrorMsg(caoyxRpcResponse.getErrorMsg());
                    rpcResponse.setResult(caoyxRpcResponse.getResult());
                } catch (InterruptedException e) {
                    rpcResponse.setStatus(CaoyxRpcStatus.FAIL);
                    rpcResponse.setErrorMsg(e.getMessage());
                    log.error(e.getMessage(), e);
                }
                break;
            case FUTURE:
                CaoyxRpcFuture caoyxRpcFuture = new CaoyxRpcFuture();
                caoyxRpcFuture.setFutureResponse(futureResponse);
                caoyxRpcFuture.setTimeout(rpcRequest.getTimeout());
                caoyxRpcFuture.setUnit(TimeUnit.MILLISECONDS);
                CaoyxRpcFuture.setFuture(caoyxRpcFuture);
                rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                break;
            case CALLBACK:
                rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                futureResponse.setCaoyxRpcInvokerCallBack(invocation.getCaoyxRpcInvokerCallBack());
                break;
            case ONE_WAY:
                rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                CaoyxRpcPendingInvokerPool.INSTANCE.removeInvokerFuture(rpcRequest.getRequestId());
                break;
            default:
                throw new CaoyxRpcException("unSupport call Type: [" + invocation.getCallType() + "]");
        }
        if (invocation.getCaoyxRpcInvokerFailBack() != null) {
            switch (rpcResponse.getStatus()) {
                case FAIL:
                case ILLEGAL_ACCESSS_TOKEN:
                case ILLEHAL_METHOD:
                    rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                    rpcResponse.setResult(invocation.getCaoyxRpcInvokerFailBack().onFail(rpcResponse.getErrorMsg()));
                    break;
                case RATE_LIMIT:
                    rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                    rpcResponse.setResult(invocation.getCaoyxRpcInvokerFailBack().onRateLimit());
                    break;
                case TIMEOUT:
                    rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                    rpcResponse.setResult(invocation.getCaoyxRpcInvokerFailBack().onTimeout());
                    break;
            }
        }
        for (int j = invocation.getRpcFilters().size() - 1; j >= 0; j--) {
            try {
                invocation.getRpcFilters().get(j).invokeResponseHandler(rpcResponse);
            } catch (Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
            }
        }
        return rpcResponse;
    }
}