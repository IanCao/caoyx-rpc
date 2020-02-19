package com.caoyx.rpc.core.data;

import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import lombok.Data;

/**
 * @author caoyixiong
 */
@Data
public class CaoyxRpcResponse extends CaoyxRpcPacket {

    private CaoyxRpcStatus status;

    private String errorMsg;

    private Object result;

    public boolean isSuccess() {
        return status != null && status == CaoyxRpcStatus.SUCCESS;
    }

    public static CaoyxRpcResponse buildIllegalResponse(String errorMsg) {
        CaoyxRpcResponse response = new CaoyxRpcResponse();
        response.setStatus(CaoyxRpcStatus.ILLEGAL);
        response.setErrorMsg(errorMsg);
        return response;
    }
}