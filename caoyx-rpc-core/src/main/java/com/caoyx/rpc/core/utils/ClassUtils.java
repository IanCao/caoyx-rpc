package com.caoyx.rpc.core.utils;


import com.sun.org.apache.xpath.internal.operations.Bool;

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

    public static boolean isPrimitive(Class type) {
        if (primClasses.containsKey(type.getName())) {
            return true;
        }
        return false;
    }

    public static String castPrimivate(String clazz, String data) {
        if (int.class.getName().equals(clazz)) {
            return "((" + Integer.class.getName() + ")" + data + ")" + ".intValue()";
        }

        if (long.class.getName().equals(clazz)) {
            return "((" + Long.class.getName() + ")" + data + ")" + ".longValue()";
        }

        if (boolean.class.getName().equals(clazz)) {
            return "((" + Boolean.class.getName() + ")" + data + ")" + ".booleanValue()";
        }

        if (double.class.getName().equals(clazz)) {
            return "((" + Double.class.getName() + ")" + data + ")" + ".doubleValue()";
        }

        if (float.class.getName().equals(clazz)) {
            return "((" + Float.class.getName() + ")" + data + ")" + ".floatValue()";
        }

        if (short.class.getName().equals(clazz)) {
            return "((" + Short.class.getName() + ")" + data + ")" + ".shortValue()";
        }

        if (byte.class.getName().equals(clazz)) {
            return "((" + Byte.class.getName() + ")" + data + ")" + ".byteValue()";
        }

        if (char.class.getName().equals(clazz)) {
            return "((" + Character.class.getName() + ")" + data + ")" + ".charValue()";
        }

        return data;
    }

    public static String box(Class<?> clazz, String value) {
        if (int.class.equals(clazz)) {
            return Integer.class.getName() + ".valueOf(" + value + ")";
        }

        if (long.class.equals(clazz)) {
            return Long.class.getName() + ".valueOf(" + value + ")";
        }

        if (boolean.class.equals(clazz)) {
            return Boolean.class.getName() + ".valueOf(" + value + ")";
        }

        if (double.class.equals(clazz)) {
            return Double.class.getName() + ".valueOf(" + value + ")";
        }

        if (float.class.equals(clazz)) {
            return Float.class.getName() + ".valueOf(" + value + ")";
        }

        if (short.class.equals(clazz)) {
            return Short.class.getName() + ".valueOf(" + value + ")";
        }

        if (byte.class.equals(clazz)) {
            return Byte.class.getName() + ".valueOf(" + value + ")";
        }

        if (char.class.equals(clazz)) {
            return Character.class.getName() + ".valueOf(" + value + ")";
        }

        return value;
    }

    public static String unbox(Class<?> clazz) {
        if (int.class.equals(clazz)) {
            return ".intValue()";
        }

        if (long.class.equals(clazz)) {
            return ".longValue()";
        }

        if (boolean.class.equals(clazz)) {
            return ".booleanValue()";
        }

        if (double.class.equals(clazz)) {
            return ".doubleValue()";
        }

        if (float.class.equals(clazz)) {
            return ".floatValue()";
        }

        if (short.class.equals(clazz)) {
            return ".shortValue()";
        }

        if (byte.class.equals(clazz)) {
            return ".byteValue()";
        }

        if (char.class.equals(clazz)) {
            return ".charValue()";
        }

        return "";
    }

    public static boolean isVoid(Class clazz) {
        return "void".equals(clazz.getName()) || "java.lang.Void".equals(clazz.getName());
    }
}