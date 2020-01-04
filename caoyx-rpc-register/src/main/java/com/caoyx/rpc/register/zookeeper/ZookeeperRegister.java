package com.caoyx.rpc.register.zookeeper;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:19
 */
@Slf4j
@Implement(name = "zookeeper")
public class ZookeeperRegister extends CaoyxRpcRegister implements IZkChildListener {

    private static final String ROOT_PATH = "/CaoyxRpc";
    private static final String SPLIT = "/";

    private ZkClient zkClient;

    public void initRegisterConnect(String address) {
        zkClient = new ZkClient(address);
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.create(ROOT_PATH, null, CreateMode.PERSISTENT);
        }
    }

    public void register(String ip, int port) {
        String applicationPath = ROOT_PATH + SPLIT + this.applicationName;
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

    protected Set<Address> fetchAllAddress(String applicationName, String version) {
        List<String> childNodes = zkClient.getChildren(buildApplicationPath());
        if (childNodes == null || childNodes.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        List<Address> result = new ArrayList<Address>();
        for (String childNode : childNodes) {
            String[] ipPort = childNode.split(":");
            result.add(new Address(ipPort[0], Integer.valueOf(ipPort[1])));
        }
        log.info("fetchAllAddress - applicationName:[" + applicationName + "]，version:[" + version + "]: " + result.toString());
        return new HashSet<Address>(result);
    }

    @Override
    protected void doStop() {
        if (zkClient != null) {
            zkClient.close();
        }
    }

    private String buildApplicationPath() {
        return ROOT_PATH + "/" + applicationName + "/" + version;
    }

    public void handleChildChange(String s, List<String> list) throws Exception {
        if (!s.contains(buildApplicationPath())) {
            return;
        }
        log.info("path:[" + s + "] child change");
        fetch(applicationName, version);
    }
}