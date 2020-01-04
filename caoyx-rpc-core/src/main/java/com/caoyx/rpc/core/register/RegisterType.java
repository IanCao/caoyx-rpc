package com.caoyx.rpc.core.register;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-04 11:31
 */
public enum RegisterType {
    NO_REGISTER("noRegister"),
    ZOOKEEPER("zookeeper");

    private String value;

    RegisterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}