package com.caoyx.rpc.admin.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-12 23:13
 */
public class JsonUtils {
    private static final Gson gson = new Gson();

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
}