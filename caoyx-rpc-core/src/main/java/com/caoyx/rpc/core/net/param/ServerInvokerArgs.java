package com.caoyx.rpc.core.net.param;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import lombok.Data;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-14 19:47
 */
@Data
public class ServerInvokerArgs {
    private CaoyxRpcRequest requestPacket;
    private Address invokerAddress;

}