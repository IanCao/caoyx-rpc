package com.caoyx.rpc.core.invoker;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author caoyixiong
 */
@Slf4j
public class CaoyxRpcFutureResponse implements Future<CaoyxRpcResponse> {

    private final CaoyxRpcRequest request;
    private volatile CaoyxRpcResponse response;

    @Setter
    @Getter
    private volatile CaoyxRpcInvokerCallBack caoyxRpcInvokerCallBack;  //TODO 有线程问题

    private volatile boolean done = false;
    private final Object lock = new Object();

    public CaoyxRpcFutureResponse(CaoyxRpcRequest request) {
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public void notifyResponse(CaoyxRpcResponse response) {
        synchronized (lock) {
            this.response = response;
            done = true;
            lock.notifyAll();
        }
        if (caoyxRpcInvokerCallBack != null) {
            if (response.getStatus() == CaoyxRpcStatus.SUCCESS) {
                caoyxRpcInvokerCallBack.onSuccess(response.getResult());
            } else if (response.getStatus() == CaoyxRpcStatus.FAIL
                    || response.getStatus() == CaoyxRpcStatus.PARAM_ERROR) {
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
        long lastTime = (TimeUnit.MILLISECONDS == unit) ? timeout : TimeUnit.MILLISECONDS.convert(timeout, unit);
        if (!done) {
            synchronized (lock) {
                if (lastTime <= 0) {
                    while (!done) {
                        lock.wait();
                    }
                } else {
                    while (!done && lastTime > 0) { // fix spurious wakeup
                        long startTime = System.currentTimeMillis();
                        lock.wait(lastTime);
                        lastTime -= (System.currentTimeMillis() - startTime);
                    }
                }
            }
        }

        if (!done) {
            CaoyxRpcResponse response = new CaoyxRpcResponse();
            response.setStatus(CaoyxRpcStatus.TIMEOUT);
            response.setErrorMsg("caoyx-rpc, request timeout at:" + System.currentTimeMillis() + ", request:" + request.toString());
            response.setRequestId(request.getRequestId());
            CaoyxRpcPendingInvokerPool.INSTANCE.removeInvokerFuture(request.getRequestId());
            return response;
        }
        return response;
    }
}