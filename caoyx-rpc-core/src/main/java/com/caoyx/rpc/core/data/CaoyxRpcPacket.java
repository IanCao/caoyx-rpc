package com.caoyx.rpc.core.data;

import lombok.Data;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 11:32
 */
@Data
public abstract class CaoyxRpcPacket implements Packet {

    byte serializerType;

    byte compressType;

    private String requestId;

    private String accessToken;
}