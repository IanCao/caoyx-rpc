package com.caoyx.rpc.core.invoker;


/**
 * @Author: caoyixiong
 * @Date: 2020-01-09 17:46
 */
public interface CaoyxRpcInvokerCallBack<T> {

    void onSuccess(T result);

    void onFail(String errorMsg);
}