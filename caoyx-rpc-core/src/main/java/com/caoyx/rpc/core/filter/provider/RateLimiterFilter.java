package com.caoyx.rpc.core.filter.provider;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcRateLimitException;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-30 23:51
 */
public class RateLimiterFilter implements CaoyxRpcFilter {

    private final RateLimiter rateLimiter;

    public RateLimiterFilter(int rate) {
        rateLimiter = RateLimiter.create(rate);
    }

    @Override
    public void invokeRequestHandler(CaoyxRpcRequest rpcRequest) {
        if (!rateLimiter.tryAcquire(1, rpcRequest.getTimeout(), TimeUnit.MILLISECONDS)) {
            throw new CaoyxRpcRateLimitException("rate Limiter");
        }
    }

    @Override
    public void invokeResponseHandler(CaoyxRpcResponse rpcResponse) {

    }
}