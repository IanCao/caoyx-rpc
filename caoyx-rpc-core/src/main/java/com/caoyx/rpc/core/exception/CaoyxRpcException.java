package com.caoyx.rpc.core.exception;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-16 20:21
 */
public class CaoyxRpcException extends Exception {
    public CaoyxRpcException(String msg) {
        super(msg);
    }

    public CaoyxRpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaoyxRpcException(Throwable cause) {
        super(cause);
    }
}