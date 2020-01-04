package com.caoyx.rpc.core.invoker;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author caoyixiong
 */
public class CaoyxRpcFutureResponse implements Future<CaoyxRpcResponse> {

    private CaoyxRpcInvokerFactory invokerFactory;

    private CaoyxRpcRequest request;
    private CaoyxRpcResponse response;

    private boolean done = false;
    private final Object lock = new Object();

    public CaoyxRpcFutureResponse(final CaoyxRpcInvokerFactory invokerFactory, CaoyxRpcRequest request) {
        this.invokerFactory = invokerFactory;
        this.request = request;

        setInvokerFuture();
    }

    private void setInvokerFuture() {
        invokerFactory.setInvokerFuture(request.getRequestId(), this);
    }

    public void removeInvokerFuture() {
        this.invokerFactory.removeInvokerFuture(request.getRequestId());
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public void setResponse(CaoyxRpcResponse response) {
        this.response = response;
        synchronized (lock) {
            done = true;
            lock.notifyAll();
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
            return response;
        }
        return response;
    }
}