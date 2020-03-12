package com.caoyx.rpc.core.register.impl.zookeeper;

import com.caoyx.rpc.core.constant.Constants;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.NotifyListener;
import com.caoyx.rpc.core.url.register.InvokerURL;
import com.caoyx.rpc.core.url.register.ProviderURL;
import com.caoyx.rpc.core.url.URL;
import com.caoyx.rpc.core.url.register.RegisterURL;
import com.caoyx.rpc.core.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:19
 */
@Slf4j
@Implement(name = "zookeeper")
public class ZookeeperRegister extends CaoyxRpcRegister {

    private static final String ROOT_PATH = "/CaoyxRpc";
    private static final String SPLIT = "/";

    private ConcurrentHashMap<String, IZkChildListener> url2Listener = new ConcurrentHashMap<>();

    private ZkClient zkClient;

    @Override
    protected void initRegisterConnect() {
        zkClient = new ZkClient(getAddress());
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.create(ROOT_PATH, null, CreateMode.PERSISTENT);
        }
    }


    @Override
    protected void doRegisterInvoker(InvokerURL url) {
        // /CaoyxRpc/providerApplicationName/className@1/invoker/
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(ROOT_PATH)
                .append(SPLIT)
                .append(getProviderApplicationName())
                .append(SPLIT)
                .append(url.getClassKey())
                .append(SPLIT)
                .append(url.getProtocol().getLabel());

        zkClient.createPersistent(pathBuilder.toString(), true);

        // /CaoyxRpc/providerApplicationName/className-1/invoker/ip:port
        pathBuilder.append(SPLIT).append(url.getHostPort());
        if (zkClient.exists(pathBuilder.toString())) {
            return;
        }
        zkClient.createEphemeral(pathBuilder.toString(), url.getMetadata());
    }

    @Override
    protected void doRegisterProvider(ProviderURL url) {
        // /CaoyxRpc/caoyxRpc-server/className@1/provider/
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(ROOT_PATH)
                .append(SPLIT)
                .append(getApplicationName())
                .append(SPLIT)
                .append(url.getClassKey())
                .append(SPLIT)
                .append(url.getProtocol().getLabel());

        zkClient.createPersistent(pathBuilder.toString(), true);

        // /CaoyxRpc/caoyxRpc-server/className-1/provider/ip:port
        pathBuilder.append(SPLIT).append(url.getHostPort());
        if (zkClient.exists(pathBuilder.toString())) {
            return;
        }
        zkClient.createEphemeral(pathBuilder.toString(), url.getMetadata());
    }


    @Override
    protected void doUnRegisterProvider(ProviderURL url) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(ROOT_PATH)
                .append(SPLIT)
                .append(getApplicationName())
                .append(SPLIT)
                .append(url.getClassKey())
                .append(SPLIT)
                .append(url.getProtocol().getLabel())
                .append(SPLIT).append(url.getHostPort());
        if (!zkClient.exists(pathBuilder.toString())) {
            return;
        }
        zkClient.delete(pathBuilder.toString());
    }

    private String url2SubscribePath(InvokerURL url) {
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

    @Override
    protected void doSubscribe(final InvokerURL url, final NotifyListener listener) {
        String subscribePath = url2SubscribePath(url);
        if (url2Listener.containsKey(subscribePath)) {
            return;
        }
        url2Listener.putIfAbsent(subscribePath, new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {

                if (CollectionUtils.isEmpty(list)) {
                    listener.onChange(url.getClassKey(), Collections.<ProviderURL>emptyList());
                    return;
                }
                String[] data = s.split("/");
                List<ProviderURL> urls = new ArrayList<>();
                for (String string : list) {
                    // /CaoyxRpc/caoyxRpc-server/className@1/provider/ip:port
                    ProviderURL providerURL = new ProviderURL();
                    providerURL.setApplicationName(data[2]);
                    providerURL.setHostPort(string);
                    providerURL.setClassName(data[3].split("@")[0]);
                    providerURL.setImplVersion(Integer.valueOf(data[3].split("@")[1]));

                    urls.add(providerURL);
                }
                listener.onChange(url.getClassKey(), urls);
            }
        });
        zkClient.subscribeChildChanges(subscribePath, url2Listener.get(subscribePath));
    }

    @Override
    protected void doUnsubscribe(InvokerURL url, NotifyListener listener) {
        String subscribePath = url2SubscribePath(url);
        if (!url2Listener.containsKey(subscribePath)) {
            return;
        }
        zkClient.unsubscribeChildChanges(subscribePath, url2Listener.get(subscribePath));
        url2Listener.remove(subscribePath);
        listener.onChange(url.getClassKey(), Collections.<ProviderURL>emptyList());
    }

    @Override
    protected void doStop() {
        if (zkClient != null) {
            zkClient.close();
        }
    }

    @Override
    public List<ProviderURL> getProviderURLsByInvokerURL(InvokerURL invokerURL) {
        String providerUrlString = url2SubscribePath(invokerURL);
        if (!zkClient.exists(providerUrlString)) {
            return Collections.EMPTY_LIST;
        }
        List<String> providers = zkClient.getChildren(providerUrlString);
        if (CollectionUtils.isEmpty(providers)) {
            return Collections.EMPTY_LIST;
        }
        List<ProviderURL> urls = new ArrayList<>();
        for (String provider : providers) {
            ProviderURL providerURL = new ProviderURL();
            providerURL.setApplicationName(invokerURL.getProviderApplicationName());
            providerURL.setHostPort(provider);
            providerURL.setClassName(invokerURL.getClassName());
            providerURL.setImplVersion(invokerURL.getImplVersion());
            urls.add(providerURL);
        }
        return urls;
    }
}