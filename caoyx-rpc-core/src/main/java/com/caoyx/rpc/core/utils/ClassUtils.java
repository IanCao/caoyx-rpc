package com.caoyx.rpc.core.utils;

import java.util.HashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-10 14:01
 */
public class ClassUtils {
    private static final HashMap<String, Class<?>> primClasses = new HashMap<>();

    static {
        primClasses.put("boolean", boolean.class);
        primClasses.put("byte", byte.class);
        primClasses.put("char", char.class);
        primClasses.put("short", short.class);
        primClasses.put("int", int.class);
        primClasses.put("long", long.class);
        primClasses.put("float", float.class);
        primClasses.put("double", double.class);
        primClasses.put("void", void.class);
    }

    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            Class<?> cl = primClasses.get(className);
            if (cl != null) {
                return cl;
            } else {
                throw ex;
            }
        }
    }
}