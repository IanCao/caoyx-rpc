package com.caoyx.rpc.core.loadbalance;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.extension.annotation.SPI;

import java.util.List;
import java.util.Set;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:46
 */
@SPI(type = ExtensionType.LOADBALANCE)
public interface LoadBalance {
    Address loadBalance(String invokerInfo, Set<Address> addresses);
}