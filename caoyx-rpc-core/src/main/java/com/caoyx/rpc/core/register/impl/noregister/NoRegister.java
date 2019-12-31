package com.caoyx.rpc.core.register.impl.noregister;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 12:50
 */
public class NoRegister extends CaoyxRpcRegister {

    private CopyOnWriteArrayList<Address> addresses = new CopyOnWriteArrayList<>();

    public NoRegister() {
    }

    public NoRegister(String ip, int port) {
        addresses.add(new Address(ip, port));
    }

    @Override
    protected List<Address> fetchAllAddress(String applicationName, String version) {
        return addresses;
    }

    @Override
    public List<Address> getAllRegister(String applicationName, String version) {
        return addresses;
    }

    @Override
    public void startRegisterLoopFetch() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    public void initRegisterConnect(String address) {

    }

    @Override
    public void register(String ip, int port) {

    }
}