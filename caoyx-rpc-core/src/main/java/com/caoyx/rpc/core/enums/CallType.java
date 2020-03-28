package com.caoyx.rpc.core.enums;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 18:21
 */
public enum CallType {
    UNKNOWN(0),
    SYNC(1),
    FUTURE(2),
    CALLBACK(3),
    ONE_WAY(4);

    private int value;

    CallType(int value) {
        this.value = value;
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
}