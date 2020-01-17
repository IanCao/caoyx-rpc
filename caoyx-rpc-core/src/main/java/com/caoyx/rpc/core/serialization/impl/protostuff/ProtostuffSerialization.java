package com.caoyx.rpc.core.serialization.impl.protostuff;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.serialization.api.Serialization;
import com.caoyx.rpc.core.serialization.api.SerializerAlgorithm;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-17 12:17
 */
@Slf4j
@Implement(name = "protostuff")
public class ProtostuffSerialization implements Serialization {
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.PROTOSTUFF.getAlgorithmId();
    }

    @Override
    public <T> byte[] serialize(T object) throws CaoyxRpcException {
        RuntimeSchema<T> schema;
        LinkedBuffer buffer = null;
        byte[] result;
        try {
            schema = RuntimeSchema.createFrom((Class<T>) object.getClass());
            buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
            result = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            if (buffer != null) {
                buffer.clear();
            }
        }

        return result;
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws CaoyxRpcException {
        RuntimeSchema<T> schema;
        T newInstance;
        try {
            schema = RuntimeSchema.createFrom(clazz);
            newInstance = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(bytes, newInstance, schema);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return newInstance;
    }
}