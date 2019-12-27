package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.Address;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:18
 */
public interface Register {

    void register(String applicationName, String ip, int port, int version);

    List<Address> getAllRegister(String applicationName, int version);

    void stop();
}