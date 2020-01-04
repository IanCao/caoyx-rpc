package com.caoyx.rpc.register.noregister;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 12:50
 */
@Implement(name = "noRegister")
public class NoRegister extends CaoyxRpcRegister {

    @Override
    protected Set<Address> fetchAllAddress(String applicationName, String version) {
        return null;
    }

    @Override
    public void startRegisterLoopFetch() {

    }

    @Override
    protected void doStop() {

    }

    public void initRegisterConnect(String address) {

    }

    public void register(String ip, int port) {

    }
}