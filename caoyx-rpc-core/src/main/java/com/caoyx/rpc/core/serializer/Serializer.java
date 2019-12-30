package com.caoyx.rpc.core.serializer;

/**
 * @author caoyixiong
 */
public interface Serializer {

    /**
     * 序列化算法 取具体的序列化算法标识
     */
    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成Java对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}