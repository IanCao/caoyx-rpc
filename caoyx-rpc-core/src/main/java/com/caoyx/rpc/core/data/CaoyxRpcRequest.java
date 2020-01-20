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

    private String applicationName;

    private String applicationVersion;

    private String className;

    private String implVersion;

    private String methodName;

    private String[] parameterTypes;

    private Object[] parameters;

    private long createdTimeMills;

    private long timeout;

    private String invokerAddress;

    private String accessToken;

    private Map<String, Object> metaData = new HashMap<>();

    public String getInvokerInfo() {
        return this.applicationName + "@" + this.className + "@" + this.methodName;
    }

    @Override
    public String toString() {
        return "CaoyxRpcRequest{" +
                "requestId='" + getRequestId() + '\'' +
                "applicationName='" + applicationName + '\'' +
                ", applicationVersion='" + applicationVersion + '\'' +
                ", className='" + className + '\'' +
                ", implVersion='" + implVersion + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                ", createdTimeMills=" + createdTimeMills +
                ", timeout=" + timeout +
                ", invokerAddress='" + invokerAddress + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", metaData=" + metaData +
                '}';
    }
}