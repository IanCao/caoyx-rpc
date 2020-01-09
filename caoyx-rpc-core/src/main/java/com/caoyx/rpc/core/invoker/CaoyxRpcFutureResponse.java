package com.caoyx.rpc.core.invoker;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author caoyixiong
 */
@Slf4j
public class CaoyxRpcFutureResponse implements Future<CaoyxRpcResponse> {

    private CaoyxRpcRequest request;
    private CaoyxRpcResponse response;

    @Setter
    @Getter
    private CaoyxRpcInvokerCallBack caoyxRpcInvokerCallBack;

    private boolean done = false;
    private final Object lock = new Object();

    public CaoyxRpcFutureResponse(CaoyxRpcRequest request) {
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public void notifyResponse(CaoyxRpcResponse response) {
        this.response = response;
        synchronized (lock) {
            done = true;
            lock.notifyAll();
        }
        if (caoyxRpcInvokerCallBack != null) {
            if (response.getStatus() == CaoyxRpcStatus.SUCCESS) {
                caoyxRpcInvokerCallBack.onSuccess(response.getResult());
            } else if (response.getStatus() == CaoyxRpcStatus.FAIL) {
                caoyxRpcInvokerCallBack.onFail(response.getErrorMsg());
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public CaoyxRpcResponse get() throws InterruptedException {
        return get(-1, TimeUnit.MILLISECONDS);
    }

    @Override
    public CaoyxRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException {
        if (!done) {
            synchronized (lock) {
                if (timeout < 0) {
                    lock.wait();
                } else {
                    long timeoutMillis = (TimeUnit.MILLISECONDS == unit) ? timeout : TimeUnit.MILLISECONDS.convert(timeout, unit);
                    lock.wait(timeoutMillis);
                }
            }
        }
        if (!done) {
            CaoyxRpcResponse response = new CaoyxRpcResponse();
            response.setStatus(CaoyxRpcStatus.TIMEOUT);
            response.setErrorMsg("caoyx-rpc, request timeout at:" + System.currentTimeMillis() + ", request:" + request.toString());
            response.setRequestId(request.getRequestId());
            log.warn(response.getErrorMsg());
            return response;
        }
        return response;
    }
}