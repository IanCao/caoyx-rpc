package com.caoyx.rpc.core.loadbalance;

import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.extension.annotation.SPI;
import com.caoyx.rpc.core.url.URL;
import com.caoyx.rpc.core.url.register.ProviderURL;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:46
 */
@SPI(type = ExtensionType.LOADBALANCE)
public interface LoadBalance {
    ProviderURL loadBalance(String classWithMethodKey, List<ProviderURL> providerURLs);
}