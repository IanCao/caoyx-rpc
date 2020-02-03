package com.caoyx.rpc.core.serialization.impl.jdk;


import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.serialization.Serialization;
import com.caoyx.rpc.core.serialization.SerializerType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * @author caoyixiong
 */
@Implement(name = "jdk")
public class JDKSerialization implements Serialization {

    @Override
    public byte getSerializerType() {
        return SerializerType.JDK.getType();
    }

    @Override
    public <T> byte[] serialize(T object) throws CaoyxRpcException {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new CaoyxRpcException(e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                throw new CaoyxRpcException(e);
            }
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws CaoyxRpcException, CaoyxRpcException {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new CaoyxRpcException(e);
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    throw new CaoyxRpcException(e);
                }
            }
        }
    }
}