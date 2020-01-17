package com.caoyx.rpc.core.serialization.api;

import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.SPI;

/**
 * @author caoyixiong
 */
@SPI(type = ExtensionType.SERIALIZATION)
public interface Serialization {

    /**
     * 序列化算法 取具体的序列化算法标识
     */
    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     */
    <T> byte[] serialize(T object) throws CaoyxRpcException;

    /**
     * 二进制转换成Java对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes) throws CaoyxRpcException;
}