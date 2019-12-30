package com.caoyx.rpc.core.data;

import lombok.Data;

/**
 * @author caoyixiong
 */
@Data
public class CaoyxRpcResponse extends CaoyxRpcPacket {

    private String errorMsg;

    private Object result;
}