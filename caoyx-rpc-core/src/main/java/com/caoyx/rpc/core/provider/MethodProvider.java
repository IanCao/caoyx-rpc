package com.caoyx.rpc.core.provider;


/**
 * @Author: caoyixiong
 * @Date: 2020-01-20 18:07
 */
public interface MethodProvider {
    Object invoke(Object[] params);
}