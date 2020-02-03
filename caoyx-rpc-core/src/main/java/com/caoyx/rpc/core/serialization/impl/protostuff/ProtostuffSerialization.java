package com.caoyx.rpc.core.serialization.impl.protoStuff;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.serialization.Serialization;
import com.caoyx.rpc.core.serialization.SerializerType;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-17 12:17
 */
@Slf4j
@Implement(name = "protoStuff")
public class ProtoStuffSerialization implements Serialization {

    private ConcurrentHashMap<String, RuntimeSchema> schemaMap = new ConcurrentHashMap<>();

    @Override
    public byte getSerializerType() {
        return SerializerType.PROTOSTUFF.getType();
    }

    @Override
    public <T> byte[] serialize(T object) throws CaoyxRpcException {
        RuntimeSchema<T> schema;
        LinkedBuffer buffer = null;
        byte[] result;
        try {
            schema = getSchema(object.getClass());
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
            schema = getSchema(clazz);
            newInstance = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(bytes, newInstance, schema);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return newInstance;
    }

    private <T> RuntimeSchema getSchema(Class<T> clazz) {
        if (schemaMap.containsKey(clazz.getName())) {
            return schemaMap.get(clazz.getName());
        }
        RuntimeSchema schema = RuntimeSchema.createFrom(clazz);
        schemaMap.putIfAbsent(clazz.getName(), schema);
        return schemaMap.get(clazz.getName());
    }
}