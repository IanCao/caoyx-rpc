package com.caoyx.rpc.core.utils;

import java.lang.reflect.Method;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-03 15:43
 */
public class MethodUtils {
    public static String generateMethodKey(Method method) {
        String methodName = method.getName();
        Class[] paramTypes = method.getParameterTypes();
        return generateMethodKey(methodName, paramTypes);
    }

    public static String generateMethodKey(String methodName, Class[] paramTypes) {
        if (paramTypes == null || paramTypes.length == 0) {
            return generateMethodKey(methodName, new String[0]);
        }
        String[] paramTypeStrings = new String[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypeStrings[i] = paramTypes[i].getName();
        }
        return generateMethodKey(methodName, paramTypeStrings);
    }

    public static String generateMethodKey(String methodName, String[] paramTypes) {
        StringBuilder builder = new StringBuilder();
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; i++) {
                builder.append(paramTypes[i]).append("|");
            }
        }
        return methodName + "@" + builder.toString();
    }
}