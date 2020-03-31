package com.caoyx.rpc.core.register.impl.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.caoyx.rpc.core.constant.Constants;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.NotifyListener;
import com.caoyx.rpc.core.url.register.InvokerURL;
import com.caoyx.rpc.core.url.register.ProviderURL;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-31 12:39
 */
@Slf4j
@Implement(name = "nacos")
public class NacosRegister extends CaoyxRpcRegister {

    private static final String ROOT_PATH = "CaoyxRpc";
    private static final String SPLIT = "_";

    private final ConcurrentHashMap<String, EventListener> url2EventListener = new ConcurrentHashMap<>();


    private NamingService naming;

    @Override
    protected void initRegisterConnect() {
        try {
            naming = NamingFactory.createNamingService(getAddress());
        } catch (NacosException e) {
            throw new CaoyxRpcException(e);
        }
    }

    @Override
    protected void doStop() {
        url2EventListener.clear();
    }

    @Override
    protected void doUnRegisterProvider(ProviderURL url) {
        StringBuilder serviceName = new StringBuilder();
        serviceName.append(ROOT_PATH)
                .append(SPLIT)
                .append(getApplicationName())
                .append(SPLIT)
                .append(url.getClassKey())
                .append(SPLIT)
                .append(url.getProtocol().getLabel());
        try {
            naming.deregisterInstance(serviceName.toString(), url.getHost(), url.getPort());
        } catch (NacosException e) {
            throw new CaoyxRpcException(e);
        }
    }

    @Override
    protected void doRegisterInvoker(InvokerURL url) {
    }

    @Override
    protected void doRegisterProvider(ProviderURL url) {
        // /CaoyxRpc/caoyxRpc-server/className@1/provider/
        StringBuilder serviceName = new StringBuilder();
        serviceName.append(ROOT_PATH)
                .append(SPLIT)
                .append(getApplicationName())
                .append(SPLIT)
                .append(url.getClassKey())
                .append(SPLIT)
                .append(url.getProtocol().getLabel());
        Instance instance = new Instance();
        instance.setServiceName(serviceName.toString());
        instance.setEphemeral(true);
        instance.setIp(url.getHost());
        instance.setPort(url.getPort());
        instance.setHealthy(true);
        instance.setMetadata(url.getMetadata());

        try {
            naming.registerInstance(serviceName.toString(), instance);
        } catch (NacosException e) {
            throw new CaoyxRpcException(e);
        }
    }

    @Override
    protected void doSubscribe(InvokerURL url, NotifyListener listener) {
        String serviceName = url2SubscribeServiceName(url);
        if (url2EventListener.containsKey(serviceName)) {
            return;
        }
        url2EventListener.putIfAbsent(serviceName, new EventListener() {
            @Override
            public void onEvent(Event event) {
                NamingEvent namingEvent = (NamingEvent) event;
                listener.onChange(url.getClassKey(), convert(namingEvent.getInstances(), serviceName));
            }
        });
        try {
            naming.subscribe(serviceName, url2EventListener.get(serviceName.toString()));
        } catch (NacosException e) {
            throw new CaoyxRpcException(e);
        }
    }

    @Override
    protected void doUnsubscribe(InvokerURL url, NotifyListener listener) {
        String serviceName = url2SubscribeServiceName(url);
        if (!url2EventListener.containsKey(serviceName)) {
            return;
        }
        try {
            naming.unsubscribe(serviceName, url2EventListener.get(serviceName));
        } catch (NacosException e) {
            throw new CaoyxRpcException(e);
        }
        url2EventListener.remove(serviceName);
    }

    @Override
    public List<ProviderURL> getProviderURLsByInvokerURL(InvokerURL invokerURL) {
        String serviceName = url2SubscribeServiceName(invokerURL);
        try {
            List<Instance> instances = naming.getAllInstances(serviceName);
            return convert(instances, serviceName);
        } catch (NacosException e) {
            throw new CaoyxRpcException(e);
        }
    }

    private List<ProviderURL> convert(List<Instance> instances, String serviceName) {
        List<ProviderURL> providerURLS = new ArrayList<>();
        if (CollectionUtils.isEmpty(instances)) {
            return providerURLS;
        }
        String[] detailInfos = serviceName.split(SPLIT);
        System.out.println(JsonUtils.toJson(detailInfos));
        for (Instance instance : instances) {
            ProviderURL providerURL = new ProviderURL();
            providerURL.setApplicationName(detailInfos[1]);
            providerURL.setHost(instance.getIp());
            providerURL.setPort(instance.getPort());
            providerURL.setClassName(detailInfos[2]);
            providerURL.setImplVersion(Integer.valueOf(detailInfos[3]));
            providerURL.setMetadata(instance.getMetadata());
            providerURLS.add(providerURL);
        }
        return providerURLS;
    }

    private String url2SubscribeServiceName(InvokerURL url) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(ROOT_PATH)
                .append(SPLIT)
                .append(getProviderApplicationName())
                .append(SPLIT)
                .append(url.getClassKey())
                .append(SPLIT)
                .append(Constants.PROVIDER);
        return pathBuilder.toString();
    }
}