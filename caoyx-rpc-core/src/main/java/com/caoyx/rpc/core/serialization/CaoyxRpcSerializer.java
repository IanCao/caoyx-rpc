package com.caoyx.rpc.core.serialization;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 12:08
 */
public enum CaoyxRpcSerializer {
    INSTANCE;

    private final ConcurrentHashMap<Byte, Serialization> serializerMap = new ConcurrentHashMap<>();

    /**
     * java 对象转换成二进制
     */
    public byte[] serialize(Object object, byte serializerAlgorithm) throws CaoyxRpcException {
        return getSerializer(serializerAlgorithm).serialize(object);
    }

    /**
     * 二进制转换成Java对象
     */
    public <T> T deserialize(Class<T> clazz, byte[] bytes, byte serializerType) throws CaoyxRpcException {
        return getSerializer(serializerType).deserialize(clazz, bytes);
    }

    private Serialization getSerializer(byte serializerType) throws CaoyxRpcException {
        Serialization serializer = serializerMap.get(serializerType);
        if (serializer != null) {
            return serializer;
        }
        serializerMap.putIfAbsent(serializerType, createSerializer(serializerType));
        return serializerMap.get(serializerType);
    }

    private Serialization createSerializer(byte serializerAlgorithmId) throws CaoyxRpcException {
        SerializerType serializerType = SerializerType.findByAlgorithmId(serializerAlgorithmId);
        if (serializerType == null) {
            throw new CaoyxRpcException(serializerAlgorithmId + " is the not support serializerType");
        }
        return (Serialization) ExtensionLoader.getExtension(Serialization.class, serializerType.getLabel()).getValidExtensionInstance();
    }
}