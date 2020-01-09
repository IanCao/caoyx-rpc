package com.caoyx.rpc.sample.springboot.server.exception;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 15:46
 */
public class CaoyxRpcProviderException extends RuntimeException {
    public CaoyxRpcProviderException(String msg) {
        super(msg);
    }

    public CaoyxRpcProviderException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaoyxRpcProviderException(Throwable cause) {
        super(cause);
    }
}