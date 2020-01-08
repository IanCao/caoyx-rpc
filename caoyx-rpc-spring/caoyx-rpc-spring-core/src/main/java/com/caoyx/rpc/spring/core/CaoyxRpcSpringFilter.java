package com.caoyx.rpc.spring.core;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-07 13:56
 */
public @interface CaoyxRpcSpringFilter {
    int order() default 0;
}