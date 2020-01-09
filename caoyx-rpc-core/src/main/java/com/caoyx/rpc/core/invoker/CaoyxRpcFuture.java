package com.caoyx.rpc.core.invoker;

import lombok.Setter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-09 11:34
 */
public class CaoyxRpcFuture implements Future {

    @Setter
    private CaoyxRpcFutureResponse futureResponse;
    @Setter
    private long timeout;
    @Setter
    private TimeUnit unit;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
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
    public Object get() throws InterruptedException, ExecutionException {
        return futureResponse.get(this.timeout, this.unit).getResult();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return futureResponse.get(timeout, unit).getResult();
    }

    private static ThreadLocal<CaoyxRpcFuture> threadInvokerFuture = new ThreadLocal<CaoyxRpcFuture>();

    public static <T> Future<T> getFuture() {
        Object future = threadInvokerFuture.get();
        if (future == null) {
            return null;
        }
        removeFuture();
        return (Future<T>) future;
    }

    public static void setFuture(CaoyxRpcFuture future) {
        threadInvokerFuture.set(future);
    }

    public static void removeFuture() {
        threadInvokerFuture.remove();
    }
}