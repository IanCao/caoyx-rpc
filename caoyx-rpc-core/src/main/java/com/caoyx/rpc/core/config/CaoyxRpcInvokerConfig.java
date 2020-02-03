package com.caoyx.rpc.core.config;

import com.caoyx.rpc.core.compress.CompressType;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerCallBack;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.loadbalance.LoadBalanceType;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.serialization.SerializerType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 16:19
 */
@Data
public class CaoyxRpcInvokerConfig {

    private Class<?> iFace;

    private CallType callType;

    private String remoteApplicationName;

    private String remoteApplicationVersion;

    private String remoteImplVersion;

    private int retryTimes;

    private long timeout;

    private RegisterConfig registerConfig;

    private LoadBalanceType loadBalanceType;

    private SerializerType serializerType;

    private CompressType compressType;

    private CaoyxRpcInvokerCallBack caoyxRpcInvokerCallBack;

    private String accessToken;

    private CaoyxRpcInvokerFailBack caoyxRpcInvokerFailBack;

    private List<CaoyxRpcFilter> rpcFilters = new ArrayList<>();
}