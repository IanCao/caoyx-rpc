package com.caoyx.rpc.core.utils;

import java.util.Collection;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-03 14:28
 */
public class CollectionUtils {
    public static <T> Collection defaultIfEmpty(Collection<T> collection, Collection defaultValue) {
        if (collection == null || collection.isEmpty()) {
            return defaultValue;
        }
        return collection;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }
}