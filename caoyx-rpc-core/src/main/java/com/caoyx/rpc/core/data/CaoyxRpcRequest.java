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

    private Map<String, Object> metaData = new HashMap<>();

    public String getClassWithMethodKey() {
        return this.className + "@" + this.methodKey;
    }

    @Override
    public String toString() {
        return "CaoyxRpcRequest{" +
                "requestId='" + getRequestId() + '\'' +
                ", className='" + className + '\'' +
                ", implVersion='" + implVersion + '\'' +
                ", methodKey='" + methodKey + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", createdTimeMills=" + createdTimeMills +
                ", timeout=" + timeout +
                ", metaData=" + metaData +
                '}';
    }
}