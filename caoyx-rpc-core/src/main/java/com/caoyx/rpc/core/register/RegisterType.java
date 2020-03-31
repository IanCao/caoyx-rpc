package com.caoyx.rpc.core.register;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-04 11:31
 */
public enum RegisterType {
    DIRECT("direct"),
    ZOOKEEPER("zookeeper"),
    NACOS("nacos");

    private String value;

    RegisterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static RegisterType findByValue(String value) {
        RegisterType[] types = values();
        for (int i = 0; i < types.length; i++) {
            if (types[i].value.equals(value)) {
                return types[i];
            }
        }
        return null;
    }
}