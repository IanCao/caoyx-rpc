package com.caoyx.rpc.core.register.impl.direct;

import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.NotifyListener;
import com.caoyx.rpc.core.url.register.InvokerURL;
import com.caoyx.rpc.core.url.register.ProviderURL;
import com.caoyx.rpc.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 12:50
 */
@Implement(name = "direct")
public class Direct extends CaoyxRpcRegister {

    @Override
    protected void doStop() {

    }


    @Override
    protected void initRegisterConnect() {

    }

    @Override
    protected void doUnRegisterProvider(ProviderURL url) {

    }

    @Override
    protected void doRegisterInvoker(InvokerURL url) {

    }

    @Override
    protected void doRegisterProvider(ProviderURL url) {

    }

    @Override
    protected void doSubscribe(InvokerURL url, NotifyListener listener) {

    }

    @Override
    protected void doUnsubscribe(InvokerURL url, NotifyListener listener) {
        listener.onChange(url.getClassKey(), Collections.<ProviderURL>emptyList());

    }

    @Override
    public List<ProviderURL> getProviderURLsByInvokerURL(InvokerURL invokerURL) {
        if (StringUtils.isBlank(getAddress())) {
            return Collections.EMPTY_LIST;
        }
        String[] addresses = getAddress().split(";");
        if (addresses.length == 0) {
            return Collections.EMPTY_LIST;
        }

        List<ProviderURL> providerUrls = new ArrayList<>();
        for (int i = 0; i < addresses.length; i++) {
            ProviderURL provider = new ProviderURL();
            provider.setApplicationName(invokerURL.getProviderApplicationName());
            provider.setClassName(invokerURL.getClassName());
            provider.setImplVersion(invokerURL.getImplVersion());
            provider.setHostPort(addresses[i]);
            provider.setApplicationName(invokerURL.getApplicationName());
            providerUrls.add(provider);
        }
        return providerUrls;
    }
}