package com.caoyx.rpc.core.exception;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-31 09:59
 */
public class CaoyxRpcIllegalMethodException extends CaoyxRpcException {
    public CaoyxRpcIllegalMethodException(String msg) {
        super(msg);
    }

    public CaoyxRpcIllegalMethodException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaoyxRpcIllegalMethodException(Throwable cause) {
        super(cause);
    }
}