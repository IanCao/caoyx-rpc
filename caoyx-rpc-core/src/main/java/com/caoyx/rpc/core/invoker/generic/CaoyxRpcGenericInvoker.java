package com.caoyx.rpc.core.invoker.generic;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-10 13:48
 */
public interface CaoyxRpcGenericInvoker {

    Object invoke(String IFace, String version, String methodName, String[] parameterTypes, Object[] args);
}