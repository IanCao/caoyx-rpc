package com.caoyx.rpc.core.data;

import lombok.Data;

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
}