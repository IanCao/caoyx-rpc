package com.caoyx.rpc.core.rebalance;

import com.caoyx.rpc.core.data.Address;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:46
 */
public interface Rebalance {
    Address rebalance(List<Address> addresses);
}