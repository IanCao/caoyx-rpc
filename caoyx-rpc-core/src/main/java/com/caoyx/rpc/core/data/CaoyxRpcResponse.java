package com.caoyx.rpc.core.data;

import lombok.Data;

/**
 * @author caoyixiong
 */
@Data
public class CaoyxRpcResponse extends Packet {

    private String requestId;

    private String errorMsg;

    private Object result;
}