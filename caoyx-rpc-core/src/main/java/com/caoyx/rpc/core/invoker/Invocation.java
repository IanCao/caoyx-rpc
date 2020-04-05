package com.caoyx.rpc.core.invoker;

import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.net.api.Client;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-04-03 12:54
 */
@Data
public class Invocation {
    private CallType callType;
    private Client clientInstance;
    private CaoyxRpcInvokerCallBack caoyxRpcInvokerCallBack;
    private CaoyxRpcInvokerFailBack caoyxRpcInvokerFailBack;
    private List<CaoyxRpcFilter> rpcFilters = new ArrayList<>();
}