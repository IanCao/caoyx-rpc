package com.caoyx.rpc.core.loadbalance.impl;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.loadbalance.LoadBalance;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:47
 */
@Implement(name = "random")
public class RandomLoadBalance implements LoadBalance {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    public Address loadBalance(String invokerInfo, Set<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        return (Address) addresses.toArray()[random.nextInt(addresses.size())];
    }
}