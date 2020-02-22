package com.caoyx.rpc.core.enums;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 18:21
 */
public enum CallType {
    SYNC("sync"),
    FUTURE("future"),
    CALLBACK("callback"),
    ;

    private String value;

    CallType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CallType findByValue(String value) {
        for (CallType callType : values()) {
            if(callType.value.equals(value)){
                return callType;
            }
        }
        return null;
    }
}