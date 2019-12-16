package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.netty.server.NettyServer;
import com.caoyx.rpc.core.netty.server.Server;
import com.caoyx.rpc.core.serializer.Serializer;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caoyixiong
 */
@Data
public class CaoyxRpcProviderFactory {

    private Class<? extends Server> server = NettyServer.class;
    private Class<? extends Serializer> serializer = JDKSerializerImpl.class;

    private int port = 1118;
    private String accessToken = null;

    private Server serverInstance;
    private Serializer serializerInstance;

    public void init() throws IllegalAccessException, InstantiationException, InterruptedException {
        serverInstance = server.newInstance();
        serializerInstance = serializer.newInstance();
        serverInstance.start(this);
    }

    private Map<String, Object> serviceBeanMap = new HashMap<String, Object>();

    public void addServiceBean(String className, int version, Object service) {
        String key = className + "@" + version;
        serviceBeanMap.putIfAbsent(key, service);
    }

    public Object getServiceBean(String className, int version) {
        String key = className + "@" + version;
        return serviceBeanMap.get(key);
    }

    public CaoyxRpcResponse invokeService(CaoyxRpcRequest requestPacket) {
        System.out.println("invokeService CaoyxRpcRequest: " + requestPacket.getClassName());
        CaoyxRpcResponse responsePacket = new CaoyxRpcResponse();
        responsePacket.setRequestId(requestPacket.getRequestId());

        Object serviceBean = getServiceBean(requestPacket.getClassName(), requestPacket.getVersion());

        Class clazz = serviceBean.getClass();
        String methodName = requestPacket.getMethodName();
        Class<?>[] parameterTypes = requestPacket.getParameterTypes();
        Object[] parameters = requestPacket.getParameters();

        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceBean, parameters);
            responsePacket.setResult(result);
        } catch (Throwable e) {
            responsePacket.setErrorMsg(e.getLocalizedMessage());
        }

        return responsePacket;
    }
}