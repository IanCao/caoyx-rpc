package com.caoyx.rpc.core.loadbalance;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.extension.annotation.SPI;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:46
 */
@SPI(type = ExtensionType.LOADBALANCE)
public interface LoadBalance {
    Address loadBalance(List<Address> addresses);
}