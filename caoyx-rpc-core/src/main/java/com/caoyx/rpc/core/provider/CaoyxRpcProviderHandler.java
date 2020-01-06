package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.filter.RpcFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 18:36
 */
public class CaoyxRpcProviderHandler implements RpcFilter {

    private ConcurrentHashMap<String, Object> serviceBeanMap = new ConcurrentHashMap<String, Object>();

    public void addServiceBean(String className, String version, Object service) {
        String key = className + "@" + version;
        serviceBeanMap.putIfAbsent(key, service);
    }

    public Object getServiceBean(String className, String version) {
        String key = className + "@" + version;
        return serviceBeanMap.get(key);
    }

    @Override
    public void invokeRequestHandler(CaoyxRpcRequest rpcRequest) {

    }

    @Override
    public void invokeResponseHandler(CaoyxRpcResponse rpcResponse) {

    }

    @Override
    public void doProcess(CaoyxRpcRequest requestPacket, CaoyxRpcResponse responsePacket) {
        if (requestPacket == null) {
            responsePacket = new CaoyxRpcResponse();
        }
        responsePacket.setRequestId(requestPacket.getRequestId());

        Object serviceBean = getServiceBean(requestPacket.getClassName(), requestPacket.getVersion());

        Class clazz = serviceBean.getClass();
        String methodName = requestPacket.getMethodName();
        Class<?>[] parameterTypes = requestPacket.getParameterTypes();
        Object[] parameters = requestPacket.getParameters();

        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceBean, parameters);
            responsePacket.setStatus(CaoyxRpcStatus.SUCCESS);
            responsePacket.setResult(result);
        } catch (InvocationTargetException e) {
            responsePacket.setStatus(CaoyxRpcStatus.FAIL);
            responsePacket.setErrorMsg(e.getTargetException().getMessage());
        } catch (Throwable e) {
            responsePacket.setStatus(CaoyxRpcStatus.FAIL);
            responsePacket.setErrorMsg(e.getMessage());
        }
    }
}