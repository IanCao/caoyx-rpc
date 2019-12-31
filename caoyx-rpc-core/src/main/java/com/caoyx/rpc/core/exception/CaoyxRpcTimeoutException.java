package com.caoyx.rpc.core.exception;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 15:07
 */
public class CaoyxRpcTimeoutException extends CaoyxRpcException {
    public CaoyxRpcTimeoutException(String msg) {
        super(msg);
    }

    public CaoyxRpcTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaoyxRpcTimeoutException(Throwable cause) {
        super(cause);
    }
}