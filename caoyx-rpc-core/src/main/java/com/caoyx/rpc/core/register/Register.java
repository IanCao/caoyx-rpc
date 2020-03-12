package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.ClassKey;
import com.caoyx.rpc.core.url.register.InvokerURL;
import com.caoyx.rpc.core.url.register.ProviderURL;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:18
 */
public interface Register {

    /**
     * for Invoker register
     *
     * @param address
     * @param applicationName
     * @param providerApplicationName
     */
    void initInvokerRegister(String address, String applicationName, String providerApplicationName);

    /**
     * for Provider register
     *
     * @param address
     * @param providerApplicationName
     * @param port
     */
    void initProviderRegister(String address, String providerApplicationName, int port);

    InvokerURL registerInvoker(ClassKey classKey);

    ProviderURL registerProvider(ClassKey classKey, int port);

    void unRegisterProvider(ClassKey classKey, int port);

    List<ProviderURL> getProviderURLsByInvokerURL(InvokerURL invokerURL);

    void subscribe(InvokerURL invokerURL, NotifyListener listener);

    void unsubscribe(InvokerURL invokerURL, NotifyListener listener);

    void stop();
}