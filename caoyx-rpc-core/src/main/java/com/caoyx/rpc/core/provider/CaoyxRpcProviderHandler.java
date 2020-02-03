package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.utils.MethodUtils;
import com.caoyx.rpc.core.utils.ThrowableUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 18:36
 */
public class CaoyxRpcProviderHandler {

    private ConcurrentHashMap<String, MethodProvider> methodProviderMap = new ConcurrentHashMap<>();

    public void addServiceMethodProvider(String className, String implVersion, Object service) {
        Class clazz;
        try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new CaoyxRpcException(e);
        }

        Method[] methods = clazz.getMethods();
        if (methods.length == 0) {
            return;
        }

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodUniqueKey = generateMethodUniqueKey(className, implVersion, MethodUtils.generateMethodKey(method));
            if (methodProviderMap.containsKey(methodUniqueKey)) {
                return;
            }
            methodProviderMap.putIfAbsent(methodUniqueKey, new JavassistProvider(className, implVersion, method, service));
        }
    }

    private MethodProvider getServiceMethodProvider(String className, String implVersion, String methodKey) {
        String methodUniqueKey = generateMethodUniqueKey(className, implVersion, methodKey);
        return methodProviderMap.get(methodUniqueKey);
    }


    public CaoyxRpcResponse invoke(CaoyxRpcRequest requestPacket) {
        CaoyxRpcResponse response = new CaoyxRpcResponse();
        response.setRequestId(requestPacket.getRequestId());
        response.setSerializerType(requestPacket.getSerializerType());
        response.setCompressType(requestPacket.getCompressType());

        MethodProvider methodProvider = getServiceMethodProvider(requestPacket.getClassName(), requestPacket.getImplVersion(), requestPacket.getMethodKey());

        if (methodProvider == null) {
            response.setStatus(CaoyxRpcStatus.FAIL);
            response.setErrorMsg(requestPacket.getClassName() + ":" + requestPacket.getImplVersion() + ":" + requestPacket.getMethodKey() + " has no valid bean");
            return response;
        }
        try {
            Object result = methodProvider.invoke(requestPacket.getParameters());
            response.setStatus(CaoyxRpcStatus.SUCCESS);
            response.setResult(result);
        } catch (Throwable e) {
            response.setStatus(CaoyxRpcStatus.FAIL);
            response.setErrorMsg(ThrowableUtils.throwable2String(e));
        }
        return response;
    }

    private String generateMethodUniqueKey(String className, String implVersion, String methodKey) {
        return className + "@" + implVersion + "@" + methodKey;
    }
}