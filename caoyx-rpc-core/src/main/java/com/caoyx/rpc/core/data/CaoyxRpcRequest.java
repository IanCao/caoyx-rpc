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

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    private long createdTimeMills;

    private long timeout;

    private Address address;

    private Map<String, Object> metaData = new HashMap<>();

    public String getInvokerInfo() {
        return this.applicationName + "@" + this.className + "@" + this.methodName;
    }
}