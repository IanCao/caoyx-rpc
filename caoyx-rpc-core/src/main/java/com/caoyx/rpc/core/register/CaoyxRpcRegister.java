package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.ClassKey;
import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.SPI;
import com.caoyx.rpc.core.url.register.InvokerURL;
import com.caoyx.rpc.core.url.register.ProviderURL;
import com.caoyx.rpc.core.utils.NetUtils;
import lombok.Getter;

import java.util.Map;


/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 16:16
 */
@SPI(type = ExtensionType.REGISTER)
public abstract class CaoyxRpcRegister implements Register {

    @Getter
    private String applicationName;
    @Getter
    private String providerApplicationName;
    @Getter
    private String address;
    @Getter
    private int port;

    protected abstract void doStop();

    @Override
    public void initInvokerRegister(String address, String applicationName, String providerApplicationName) {
        this.address = address;
        this.applicationName = applicationName;
        this.providerApplicationName = providerApplicationName;
        initRegisterConnect();
    }

    @Override
    public void initProviderRegister(String address, String applicationName, int port) {
        this.address = address;
        this.applicationName = applicationName;
        this.providerApplicationName = applicationName;
        this.port = port;
        initRegisterConnect();
    }

    protected abstract void initRegisterConnect();

    @Override
    public InvokerURL registerInvoker(ClassKey classKey) {
        InvokerURL url = new InvokerURL();
        url.setClassName(classKey.getClassName());
        url.setImplVersion(classKey.getVersion());
        url.setHost(NetUtils.getLocalAddress());
        url.setPort(0);
        url.setProviderApplicationName(providerApplicationName);
        url.setApplicationName(applicationName);
        doRegisterInvoker(url);
        return url;
    }

    @Override
    public ProviderURL registerProvider(ClassKey classKey, int port, Map<String, String> metadata) {
        ProviderURL url = new ProviderURL();
        url.setClassName(classKey.getClassName());
        url.setImplVersion(classKey.getVersion());
        url.setHost(NetUtils.getLocalAddress());
        url.setPort(port);
        url.setApplicationName(applicationName);
        url.setMetadata(metadata);
        doRegisterProvider(url);
        return url;
    }


    @Override
    public void unRegisterProvider(ClassKey classKey, int port) {
        ProviderURL url = new ProviderURL();
        url.setClassName(classKey.getClassName());
        url.setImplVersion(classKey.getVersion());
        url.setHost(NetUtils.getLocalAddress());
        url.setPort(port);
        url.setApplicationName(applicationName);
        doUnRegisterProvider(url);
    }

    protected abstract void doUnRegisterProvider(ProviderURL url);

    protected abstract void doRegisterInvoker(InvokerURL url);

    protected abstract void doRegisterProvider(ProviderURL url);

    @Override
    public final void stop() {
        doStop();
    }

    @Override
    public final void subscribe(InvokerURL url, NotifyListener listener) {
        doSubscribe(url, listener);
    }

    @Override
    public final void unsubscribe(InvokerURL url, NotifyListener listener) {
        doUnsubscribe(url, listener);
    }

    protected abstract void doSubscribe(InvokerURL url, NotifyListener listener);

    protected abstract void doUnsubscribe(InvokerURL url, NotifyListener listener);
}