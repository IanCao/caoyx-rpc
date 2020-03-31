package com.caoyx.rpc.core.exception;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-31 09:54
 */
public class CaoyxRpcAccessTokenIllegalException extends CaoyxRpcException {

    public CaoyxRpcAccessTokenIllegalException(String msg) {
        super(msg);
    }

    public CaoyxRpcAccessTokenIllegalException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaoyxRpcAccessTokenIllegalException(Throwable cause) {
        super(cause);
    }
}