package com.caoyx.rpc.core.loadbalance.impl;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.loadbalance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:47
 */
public class RandomLoadBalance implements LoadBalance {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    public Address loadBalance(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        return addresses.get(random.nextInt(addresses.size()));
    }
}