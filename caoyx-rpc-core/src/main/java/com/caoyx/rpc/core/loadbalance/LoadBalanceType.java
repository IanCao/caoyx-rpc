package com.caoyx.rpc.core.loadbalance;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-13 13:58
 */
public enum LoadBalanceType {
    RANDOM("random"),
    CONSISTENT_HASH("consistentHash");

    private String value;

    LoadBalanceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static LoadBalanceType findByValue(String value) {
        for (LoadBalanceType loadBalanceType : values()) {
            if (loadBalanceType.value.equals(value)) {
                return loadBalanceType;
            }
        }
        return null;
    }
}