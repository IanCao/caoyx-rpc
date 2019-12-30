package com.caoyx.rpc.core.register.impl;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.register.Register;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:19
 */
public class ZookeeperRegister implements Register {

    private static final String ROOT_PATH = "/CaoyxRpc";
    private static final String SPLIT = "/";


    private ZkClient zkClient;

    public ZookeeperRegister(String address) {
        zkClient = new ZkClient(address);
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.create(ROOT_PATH, null, CreateMode.PERSISTENT);
        }
    }

    @Override
    public void register(String applicationName, String ip, int port, int version) {
        String applicationPath = ROOT_PATH + SPLIT + applicationName;
        if (!zkClient.exists(applicationPath)) {
            zkClient.create(applicationPath, null, CreateMode.PERSISTENT);
        }
        String applicationVersionPath = applicationPath + SPLIT + version;
        if (!zkClient.exists(applicationVersionPath)) {
            zkClient.create(applicationVersionPath, null, CreateMode.PERSISTENT);
        }
        String instancePath = applicationVersionPath + SPLIT + ip + ":" + port;
        if (!zkClient.exists(instancePath)) {
            zkClient.create(instancePath, null, CreateMode.EPHEMERAL);
        }
    }

    @Override
    public List<Address> getAllRegister(String applicationName, int version) {
        List<String> childNodes = zkClient.getChildren(buildApplicationPath(applicationName, version));
        if (childNodes == null || childNodes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Address> result = new ArrayList<>();
        for (String childNode : childNodes) {
            String[] ipPort = childNode.split(":");
            result.add(new Address(ipPort[0], Integer.valueOf(ipPort[1])));
        }
        return result;
    }

    @Override
    public void stop() {
        if (zkClient != null) {
            zkClient.close();
        }
    }

    private String buildApplicationPath(String applicationName, int version) {
        return ROOT_PATH + "/" + applicationName + "/" + version;
    }
}