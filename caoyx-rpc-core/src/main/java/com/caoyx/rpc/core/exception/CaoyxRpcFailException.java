package com.caoyx.rpc.core.exception;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 16:03
 */
public class CaoyxRpcFailException extends CaoyxRpcException {
    public CaoyxRpcFailException(String msg) {
        super(msg);
    }

    public CaoyxRpcFailException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaoyxRpcFailException(Throwable cause) {
        super(cause);
    }
}