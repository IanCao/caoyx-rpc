package com.caoyx.rpc.core.serialization;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.serialization.api.Serialization;
import com.caoyx.rpc.core.serialization.api.SerializerAlgorithm;

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
    public <T> T deserialize(Class<T> clazz, byte[] bytes, byte serializerAlgorithm) throws CaoyxRpcException {
        return getSerializer(serializerAlgorithm).deserialize(clazz, bytes);
    }

    private Serialization getSerializer(byte serializerAlgorithm) throws CaoyxRpcException {
        Serialization serializer = serializerMap.get(serializerAlgorithm);
        if (serializer != null) {
            return serializer;
        }
        serializer = createSerializer(serializerAlgorithm);
        serializerMap.put(serializerAlgorithm, serializer);
        return serializer;
    }

    private Serialization createSerializer(byte serializerAlgorithmId) throws CaoyxRpcException {
        SerializerAlgorithm algorithm = SerializerAlgorithm.findByAlgorithmId(serializerAlgorithmId);
        if (algorithm == null) {
            throw new CaoyxRpcException(serializerAlgorithmId + "is the not support serializerAlgorithm");
        }
        return (Serialization) ExtensionLoader.getExtension(Serialization.class, algorithm.getLabel()).getValidExtensionInstance();
    }
}