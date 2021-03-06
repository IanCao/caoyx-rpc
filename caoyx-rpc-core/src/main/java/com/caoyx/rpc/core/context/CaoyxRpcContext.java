package com.caoyx.rpc.core.context;

import com.caoyx.rpc.core.enums.CallType;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
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
        CaoyxRpcContext context = CURRENT_RPC_CONTEXT.get();
        if (context != null) {
            return context;
        }
        context = new CaoyxRpcContext();
        CURRENT_RPC_CONTEXT.set(context);
        return context;
    }

    public static void removeContext() {
        CURRENT_RPC_CONTEXT.remove();
    }

    @Getter
    @Setter
    private Map<String, Object> metaData = new HashMap<>();
}