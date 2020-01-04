package com.caoyx.rpc.core.loadbalance;

import com.caoyx.rpc.core.data.Address;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:46
 */
public interface LoadBalance {
    Address loadBalance(List<Address> addresses);
}