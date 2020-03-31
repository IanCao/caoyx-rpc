package com.caoyx.rpc.core.register.impl.zookeeper;

import com.caoyx.rpc.core.constant.Constants;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.NotifyListener;
import com.caoyx.rpc.core.url.register.InvokerURL;
import com.caoyx.rpc.core.url.register.ProviderURL;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
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

    private final ConcurrentHashMap<String, TreeCacheListener> url2TreeCacheListener = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TreeCache> url2TreeCache = new ConcurrentHashMap<>();

    private CuratorFramework client;

    @Override
    protected void initRegisterConnect() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        client = CuratorFrameworkFactory.builder()
                .connectString(getAddress())
                .retryPolicy(retryPolicy)
                .build();
        client.start();
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

        // /CaoyxRpc/providerApplicationName/className-1/invoker/ip:port
        pathBuilder.append(SPLIT).append(url.getHostPort());
        try {
            if (client.checkExists().forPath(pathBuilder.toString()) != null) {
                return;
            }
            client.create().withMode(CreateMode.EPHEMERAL).forPath(pathBuilder.toString(), JsonUtils.toJson(url.getMetadata()).getBytes());
        } catch (Exception e) {
            throw new CaoyxRpcException(e);
        }
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

        // /CaoyxRpc/caoyxRpc-server/className-1/provider/ip:port
        pathBuilder.append(SPLIT).append(url.getHostPort());
        try {
            if (client.checkExists().forPath(pathBuilder.toString()) != null) {
                return;
            }
            client.create().withMode(CreateMode.EPHEMERAL).forPath(pathBuilder.toString(), JsonUtils.toJson(url.getMetadata()).getBytes());
        } catch (Exception e) {
            throw new CaoyxRpcException(e);
        }
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
        try {
            if (client.checkExists().forPath(pathBuilder.toString()) != null) {
                return;
            }
            client.delete().forPath(pathBuilder.toString());
        } catch (Exception e) {
            throw new CaoyxRpcException(e);
        }
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
        if (url2TreeCacheListener.containsKey(subscribePath)) {
            return;
        }
        url2TreeCache.putIfAbsent(subscribePath, new TreeCache(client, subscribePath));
        url2TreeCacheListener.putIfAbsent(subscribePath, new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                switch (treeCacheEvent.getType()) {
                    case NODE_ADDED:
                    case NODE_UPDATED:
                    case NODE_REMOVED:
                        System.out.println(JsonUtils.toJson(treeCacheEvent));
                        List<String> childPaths = client.getChildren().forPath(subscribePath);
                        if (CollectionUtils.isEmpty(childPaths)) {
                            listener.onChange(url.getClassKey(), Collections.<ProviderURL>emptyList());
                            return;
                        }
                        String[] subscribePathInfos = subscribePath.split("/");
                        List<ProviderURL> urls = new ArrayList<>();
                        for (String childPath : childPaths) {
                            // /CaoyxRpc/caoyxRpc-server/className@1/provider/ip:port
                            ProviderURL providerURL = new ProviderURL();
                            providerURL.setApplicationName(subscribePathInfos[2]);
                            providerURL.setHost(childPath.split(":")[0]);
                            providerURL.setPort(Integer.valueOf(childPath.split(":")[1]));
                            providerURL.setClassName(subscribePathInfos[3].split("_")[0]);
                            providerURL.setImplVersion(Integer.valueOf(subscribePathInfos[3].split("_")[1]));
                            byte[] data = client.getData().forPath(subscribePath + "/" + childPath);
                            if (data != null && data.length > 0) {
                                Map<String, String> metaData = JsonUtils.fromJson(new String(data), new TypeToken<Map<String, Object>>() {
                                }.getType());
                                if (metaData.containsKey(Constants.URL_METADATA_KEY_AVALIABLE) &&
                                        metaData.get(Constants.URL_METADATA_KEY_AVALIABLE).equals(Boolean.FALSE)) {
                                    continue;
                                }
                                providerURL.setMetadata(metaData);
                            }
                            urls.add(providerURL);
                        }
                        listener.onChange(url.getClassKey(), urls);
                    default:
                        break;
                }
            }
        });
        try {
            url2TreeCache.get(subscribePath).getListenable().addListener(url2TreeCacheListener.get(subscribePath));
            url2TreeCache.get(subscribePath).start();
        } catch (Exception e) {
            url2TreeCache.remove(subscribePath);
            url2TreeCacheListener.remove(subscribePath);
            throw new CaoyxRpcException(e);
        }
    }

    @Override
    protected void doUnsubscribe(InvokerURL url, NotifyListener listener) {
        String subscribePath = url2SubscribePath(url);
        if (!url2TreeCacheListener.containsKey(subscribePath)) {
            return;
        }
        url2TreeCache.get(subscribePath).getListenable().removeListener(url2TreeCacheListener.get(subscribePath));
        url2TreeCacheListener.remove(subscribePath);
        TreeCache treeCache = url2TreeCache.remove(subscribePath);
        if (treeCache != null) {
            treeCache.close();
        }
        listener.onChange(url.getClassKey(), Collections.<ProviderURL>emptyList());
    }

    @Override
    protected void doStop() {
        for (TreeCache treeCache : url2TreeCache.values()) {
            if (treeCache != null) {
                treeCache.close();
            }
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public List<ProviderURL> getProviderURLsByInvokerURL(InvokerURL invokerURL) {
        String providerUrlString = url2SubscribePath(invokerURL);
        List<ProviderURL> urls = new ArrayList<>();
        try {
            if (client.checkExists().forPath(providerUrlString) == null) {
                return Collections.EMPTY_LIST;
            }
            List<String> providers = client.getChildren().forPath(providerUrlString);
            if (CollectionUtils.isEmpty(providers)) {
                return Collections.EMPTY_LIST;
            }
            for (String provider : providers) {
                ProviderURL providerURL = new ProviderURL();
                providerURL.setApplicationName(invokerURL.getProviderApplicationName());
                providerURL.setHost(provider.split(":")[0]);
                providerURL.setPort(Integer.valueOf(provider.split(":")[1]));
                providerURL.setClassName(invokerURL.getClassName());
                providerURL.setImplVersion(invokerURL.getImplVersion());
                byte[] bytes = client.getData().forPath(providerUrlString + "/" + provider);
                if (bytes != null && bytes.length > 0) {
                    providerURL.setMetadata(JsonUtils.fromJson(new String(bytes), new TypeToken<Map<String, Object>>() {
                    }.getType()));
                }
                urls.add(providerURL);
            }
        } catch (Exception e) {
            throw new CaoyxRpcException(e);
        }
        return urls;
    }
}