package com.caoyx.rpc.core.exception;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-30 23:56
 */
public class CaoyxRpcRateLimitException extends CaoyxRpcException {
    public CaoyxRpcRateLimitException(String msg) {
        super(msg);
    }

    public CaoyxRpcRateLimitException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaoyxRpcRateLimitException(Throwable cause) {
        super(cause);
    }
}