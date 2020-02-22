package com.caoyx.rpc.core.loadbalance.impl;

import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import com.caoyx.rpc.core.url.URL;
import com.caoyx.rpc.core.url.register.ProviderURL;

import java.util.List;
import java.util.Random;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:47
 */
@Implement(name = "random")
public class RandomLoadBalance implements LoadBalance {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    public ProviderURL loadBalance(String classWithMethodKey, List<ProviderURL> providerURLs) {
        if (providerURLs == null || providerURLs.isEmpty()) {
            return null;
        }
        return (ProviderURL) providerURLs.toArray()[random.nextInt(providerURLs.size())];
    }
}