package com.caoyx.rpc.core.utils;

import com.caoyx.rpc.core.constant.Constants;
import com.caoyx.rpc.core.exception.CaoyxRpcException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-14 17:07
 */
public class ThreadPoolUtils {

    public static ThreadPoolExecutor createThreadPool(final String name) {
        return createThreadPool(name, Constants.PROCESS_NUM, Constants.PROCESS_NUM);
    }

    public static ThreadPoolExecutor createThreadPool(final String name, int corePoolSize, int maxPoolSize) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "caoyxRpc, " + name + "-ThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new CaoyxRpcException("caoyxRpc " + name + " Thread pool is EXHAUSTED!");
                    }
                });
    }
}