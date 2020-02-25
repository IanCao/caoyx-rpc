package com.caoyx.rpc.core.loadbalance.impl;

import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import com.caoyx.rpc.core.url.URL;
import com.caoyx.rpc.core.url.register.ProviderURL;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-17 10:59
 */
@Implement(name = "consistentHash")
public class ConsistentHashLoadBalance implements LoadBalance {

    private ConcurrentHashMap<Long, ConsistentHashSelector> selectorConcurrentHashMap = new ConcurrentHashMap<>();

    @Override

    public ProviderURL loadBalance(String classWithMethodKey, List<ProviderURL> providerURLs) {
        if (providerURLs.size() == 1) {
            return providerURLs.get(0);
        }
        long invokerInfoHash = hash(classWithMethodKey);

        ConsistentHashSelector selector = selectorConcurrentHashMap.get(invokerInfoHash);
        if (selector != null && selector.getSelectorHashCode() == invokerInfoHash) {
            return selector.select(classWithMethodKey);
        }
        ConsistentHashSelector consistentHashSelector = selectorConcurrentHashMap.putIfAbsent(invokerInfoHash, new ConsistentHashSelector(providerURLs));
        if (consistentHashSelector == null) {
            return selectorConcurrentHashMap.get(invokerInfoHash).select(classWithMethodKey);
        }
        return consistentHashSelector.select(classWithMethodKey);
    }

    public static class ConsistentHashSelector {

        private static final int VIRTUAL_NODE = 16;

        @Getter
        private Long selectorHashCode;

        private TreeMap<Long, ProviderURL> hash2UrlMap = new TreeMap<>();

        ConsistentHashSelector(List<ProviderURL> urls) {
            selectorHashCode = hash(urls.toString());
            for (ProviderURL url : urls) {
                for (int i = 0; i < VIRTUAL_NODE; i++) {
                    long urlHash = hash("CAOYX_RPC-SHARD-" + url + "-NODE-" + i);
                    hash2UrlMap.put(urlHash, url);
                }
            }
        }

        ProviderURL select(String classWithMethodKey) {
            long classWithMethodKeyHash = hash(classWithMethodKey);
            Entry<Long, ProviderURL> addressEntry = hash2UrlMap.ceilingEntry(classWithMethodKeyHash);
            if (addressEntry != null) {
                return addressEntry.getValue();
            }
            return hash2UrlMap.firstEntry().getValue();
        }
    }

    private static long hash(String key) {
        // md5 byte
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try {
            keyBytes = key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unknown string :" + key, e);
        }

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        return hashCode & 0xffffffffL;
    }
}