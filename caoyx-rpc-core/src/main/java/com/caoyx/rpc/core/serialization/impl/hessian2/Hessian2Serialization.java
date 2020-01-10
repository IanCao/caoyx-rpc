package com.caoyx.rpc.core.serialization.impl.hessian2;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.serialization.api.Serialization;
import com.caoyx.rpc.core.serialization.api.SerializerAlgorithm;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @Author: caoyixiong
 * @Date: 2020-01-08 20:42
 */
@Implement(name = "hessian2")
public class Hessian2Serialization implements Serialization {


    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.HESSIAN2.getAlgorithmId();
    }

    @Override
    public byte[] serialize(Object object) throws CaoyxRpcException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        try {
            ho.writeObject(object);
            ho.flush();
            byte[] result = os.toByteArray();
            return result;
        } catch (IOException e) {
            throw new CaoyxRpcException(e);
        } finally {
            try {
                ho.close();
            } catch (IOException e) {
                throw new CaoyxRpcException(e);
            }
            try {
                os.close();
            } catch (IOException e) {
                throw new CaoyxRpcException(e);
            }
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws CaoyxRpcException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        try {
            Object result = hi.readObject();
            return (T) result;
        } catch (IOException e) {
            throw new CaoyxRpcException(e);
        } finally {
            try {
                hi.close();
            } catch (Exception e) {
                throw new CaoyxRpcException(e);
            }
            try {
                is.close();
            } catch (IOException e) {
                throw new CaoyxRpcException(e);
            }
        }
    }
}