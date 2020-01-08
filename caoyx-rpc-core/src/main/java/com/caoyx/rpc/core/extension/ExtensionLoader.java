package com.caoyx.rpc.core.extension;

import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.SPI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-03 12:01
 */
public class ExtensionLoader {

    private static ConcurrentHashMap<String, Extension> extensionMap = new ConcurrentHashMap<>();

    public static Extension getExtension(Class clazz, String name) throws CaoyxRpcException {
        if (!clazz.isAnnotationPresent(SPI.class)) {
            throw new CaoyxRpcException(clazz.getName() + "is not annotated with @SPI");
        }
        Extension extension = extensionMap.get(clazz.getName() + "@" + name);
        if (extension != null) {
            return extension;
        }
        SPI spi = (SPI) clazz.getAnnotation(SPI.class);
        if (spi.type() == ExtensionType.UNKNOW) {
            throw new CaoyxRpcException(clazz.getName() + "must set ExtensionType in @SPI");
        }
        Extension newExtension = new Extension(clazz, spi.type(), name);
        Extension existExtension = extensionMap.putIfAbsent(clazz.getName() + "@" + name, newExtension);
        return existExtension == null ? newExtension : existExtension;
    }
}