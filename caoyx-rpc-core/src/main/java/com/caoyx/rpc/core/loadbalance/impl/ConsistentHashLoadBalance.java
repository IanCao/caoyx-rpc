package com.caoyx.rpc.core.loadbalance.impl;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.Set;
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

    public Address loadBalance(String invokerInfo, Set<Address> addresses) {
        long invokerInfoHash = hash(invokerInfo);

        ConsistentHashSelector selector = selectorConcurrentHashMap.get(invokerInfoHash);
        if (selector != null && selector.getSelectorHashCode() == invokerInfoHash) {
            return selector.select(invokerInfo);
        }
        ConsistentHashSelector consistentHashSelector = selectorConcurrentHashMap.putIfAbsent(invokerInfoHash, new ConsistentHashSelector(addresses));
        if (consistentHashSelector == null) {
            return selectorConcurrentHashMap.get(invokerInfoHash).select(invokerInfo);
        }
        return consistentHashSelector.select(invokerInfo);
    }

    public static class ConsistentHashSelector {

        private static final int VIRTUAL_NODE = 16;

        @Getter
        private Long selectorHashCode;

        private TreeMap<Long, Address> addressTreeMap = new TreeMap<>();

        ConsistentHashSelector(Set<Address> addresses) {
            selectorHashCode = hash(addresses.toString());
            for (Address address : addresses) {
                for (int i = 0; i < VIRTUAL_NODE; i++) {
                    long addressHash = hash("CAOYX_RPC-SHARD-" + address + "-NODE-" + i);
                    addressTreeMap.put(addressHash, address);
                }
            }
        }

        Address select(String invokerInfo) {
            long invokerInfoHash = hash(invokerInfo);
            Entry<Long, Address> addressEntry = addressTreeMap.ceilingEntry(invokerInfoHash);
            if (addressEntry != null) {
                return addressEntry.getValue();
            }
            return addressTreeMap.firstEntry().getValue();
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