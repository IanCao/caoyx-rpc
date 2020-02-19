package com.caoyx.rpc.core.config;

import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.register.RegisterConfig;
import lombok.Data;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 16:20
 */
@Data
public class CaoyxRpcProviderConfig {

    private String applicationName;

    private int port;

    private RegisterConfig registerConfig;

    private String applicationVersion;

    private List<CaoyxRpcFilter> rpcFilters;

    private String accessToken;
}