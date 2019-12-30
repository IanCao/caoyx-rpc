package com.caoyx.rpc.core.serializer;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 12:08
 */
public enum CaoyxRpcSerializer {
    INSTANCE;

    private final ConcurrentHashMap<Byte, Serializer> serializerMap = new ConcurrentHashMap<>();

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

    private Serializer getSerializer(byte serializerAlgorithm) throws CaoyxRpcException {
        Serializer serializer = serializerMap.get(serializerAlgorithm);
        if (serializer != null) {
            return serializer;
        }
        serializer = createSerializer(serializerAlgorithm);
        serializerMap.put(serializerAlgorithm, serializer);
        return serializer;
    }

    private Serializer createSerializer(byte serializerAlgorithm) throws CaoyxRpcException {

        if (serializerAlgorithm == SerializerAlgorithm.JDK.getAlgorithmId()) {
            return new JDKSerializerImpl();
        }

        throw new CaoyxRpcException(serializerAlgorithm + "is the not support serializerAlgorithm");
    }
}