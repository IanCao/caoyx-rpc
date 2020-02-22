package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.url.register.ProviderURL;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-20 15:14
 */
public interface NotifyListener {
    void onChange(String classKey, List<ProviderURL> providers);
}