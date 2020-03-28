package com.caoyx.rpc.core.enums;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 18:21
 */
public enum CallType {
    UNKNOWN(0, "unknown"),
    SYNC(1, "sync"),
    FUTURE(2, "future"),
    CALLBACK(3, "callback"),
    ONE_WAY(4, "oneWay");

    private int value;
    private String label;

    CallType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public static CallType findByValue(int value) {
        for (CallType callType : values()) {
            if (callType.value == value) {
                return callType;
            }
        }
        return null;
    }

    public static CallType findByLabel(String label) {
        for (CallType callType : values()) {
            if (callType.label.equals(label)) {
                return callType;
            }
        }
        return null;
    }
}