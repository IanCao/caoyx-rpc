package com.caoyx.rpc.core.invoker.failback;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-17 11:33
 */
public interface CaoyxRpcInvokerFailBack<T> {
    T onFail(String errorMsg);

    T onTimeout();
}