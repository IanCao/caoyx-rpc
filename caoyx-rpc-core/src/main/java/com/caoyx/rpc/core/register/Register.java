package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.Address;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:18
 */
public interface Register {

    void initRegister(String applicationName, String version);

    void initRegisterConnect(String address);

    void startRegisterLoopFetch();

    void register(String ip, int port);

    List<Address> getAllRegister(String applicationName, String version);

    void stop();
}