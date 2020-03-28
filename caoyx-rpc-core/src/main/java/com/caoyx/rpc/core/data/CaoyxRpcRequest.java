package com.caoyx.rpc.core.data;

import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author caoyixiong
 */
@Data
public class CaoyxRpcRequest extends CaoyxRpcPacket {

    private String className;

    private int implVersion;

    private String methodKey;

    private Object[] parameters;

    private long createdTimeMills;

    private long timeout;

    private int callType;

    private Map<String, Object> metaData = new HashMap<>();

    @Override
    public String toString() {
        return "CaoyxRpcRequest{" +
                "className='" + className + '\'' +
                ", implVersion=" + implVersion +
                ", methodKey='" + methodKey + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", createdTimeMills=" + createdTimeMills +
                ", timeout=" + timeout +
                ", callType=" + callType +
                ", metaData=" + metaData +
                '}';
    }
}