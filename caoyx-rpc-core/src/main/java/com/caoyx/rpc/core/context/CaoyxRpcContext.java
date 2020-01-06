package com.caoyx.rpc.core.context;

import com.caoyx.rpc.core.data.Address;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 16:02
 */
public class CaoyxRpcContext {
    private static final FastThreadLocal<CaoyxRpcContext> CURRENT_RPC_CONTEXT = new FastThreadLocal<CaoyxRpcContext>() {
        @Override
        protected CaoyxRpcContext initialValue() {
            return new CaoyxRpcContext();
        }
    };

    public static CaoyxRpcContext getContext() {
        return CURRENT_RPC_CONTEXT.get();
    }

    public static void removeContext() {
        CURRENT_RPC_CONTEXT.remove();
    }

    @Getter
    @Setter
    private Address remoteAddress;

    @Getter
    @Setter
    private Map<String, Object> metaData;




}