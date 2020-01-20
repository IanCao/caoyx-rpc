package com.caoyx.rpc.core.data;


import com.caoyx.rpc.core.serialization.api.SerializerAlgorithm;
import lombok.Data;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 11:32
 */
@Data
public abstract class CaoyxRpcPacket implements Packet {

    byte serializerAlgorithm = SerializerAlgorithm.JDK.getAlgorithmId();

    private String requestId;

    private String accessToken;
}