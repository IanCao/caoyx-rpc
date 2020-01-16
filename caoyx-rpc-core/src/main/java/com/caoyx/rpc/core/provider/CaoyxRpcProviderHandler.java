package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.utils.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 18:36
 */
public class CaoyxRpcProviderHandler implements CaoyxRpcFilter {

    private ConcurrentHashMap<String, Object> serviceBeanMap = new ConcurrentHashMap<String, Object>();

    public void addServiceBean(String className, String implVersion, Object service) {
        String key = className + "@" + implVersion;
        serviceBeanMap.putIfAbsent(key, service);
    }

    public Object getServiceBean(String className, String implVersion) {
        String key = className + "@" + implVersion;
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

        Object serviceBean = getServiceBean(requestPacket.getClassName(), requestPacket.getImplVersion());

        Class clazz = serviceBean.getClass();
        String methodName = requestPacket.getMethodName();
        String[] parameterTypes = requestPacket.getParameterTypes();
        int parameterTypesLength = parameterTypes == null ? 0 : parameterTypes.length;
        Class<?>[] parameterClassTypes = new Class[parameterTypesLength];

        for (int i = 0; i < parameterTypesLength; i++) {
            try {
                Class parameterClass = ClassUtils.loadClass(parameterTypes[i]);
                parameterClassTypes[i] = parameterClass;
            } catch (ClassNotFoundException e) {
                responsePacket.setStatus(CaoyxRpcStatus.PARAM_ERROR);
                responsePacket.setErrorMsg(e.getMessage());
                return;
            }
        }

        Object[] parameters = requestPacket.getParameters();
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterClassTypes);
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