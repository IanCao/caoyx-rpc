package com.caoyx.rpc.core.exception;

import com.caoyx.rpc.core.enums.CaoyxRpcStatus;

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

    public static CaoyxRpcException buildByStatusAndMsg(CaoyxRpcStatus status, String msg) {
        switch (status) {
            case FAIL:
                return new CaoyxRpcFailException(msg);
            case TIMEOUT:
                return new CaoyxRpcTimeoutException(msg);
            default:
                return new CaoyxRpcException(msg);
        }
    }
}