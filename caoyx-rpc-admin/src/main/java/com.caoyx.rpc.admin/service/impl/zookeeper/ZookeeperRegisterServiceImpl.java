package com.caoyx.rpc.admin.service.impl.zookeeper;

import com.caoyx.rpc.admin.data.ClassInfo;
import com.caoyx.rpc.admin.data.InvokerInfo;
import com.caoyx.rpc.admin.data.ProviderInfo;
import com.caoyx.rpc.admin.service.RegisterService;
import com.caoyx.rpc.admin.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.caoyx.rpc.admin.constant.Constants.CAOYX_RPC;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-14 00:49
 */
@ConditionalOnProperty(name = "caoyxRpc.register.type", havingValue = "zookeeper")
@Component("zookeeperRegister")
@Slf4j
public class ZookeeperRegisterServiceImpl implements RegisterService, InitializingBean {

    private static final String ROOT_PATH = "/" + CAOYX_RPC;
    private CuratorFramework zkClient;

    @Value("${caoyxRpc.register.address}")
    private String addresses;

    @Override
    public List<String> getAllExportServices() {
        try {
            return zkClient.getChildren().forPath(ROOT_PATH);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ClassInfo> getClassInfosByServiceName(String serviceName) {
        List<ClassInfo> result = new ArrayList<>();
        try {
            List<String> data = zkClient.getChildren().forPath(ROOT_PATH + "/" + serviceName);
            if (CollectionUtils.isEmpty(data)) {
                return result;
            }
            for (String s : data) {
                ClassInfo classInfo = new ClassInfo();
                classInfo.setClassName(s.split("@")[0]);
                classInfo.setVersion(Integer.valueOf(s.split("@")[1]));
                result.add(classInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<ProviderInfo> getProviderInfosByServiceNameAndClassInfo(String serviceName, ClassInfo classInfo) {
        List<ProviderInfo> result = new ArrayList<>();
        try {
            String providerPath = ROOT_PATH + "/" + serviceName + "/" + classInfo.getClassName() + "@" + classInfo.getVersion() + "/provider";
            List<String> data = zkClient.getChildren().forPath(providerPath);
            if (CollectionUtils.isEmpty(data)) {
                return result;
            }
            for (String path : data) {
                ProviderInfo providerInfo = new ProviderInfo();
                providerInfo.setIpPort(path);
                byte[] bytes = zkClient.getData().forPath(providerPath + "/" + path);
                if (bytes != null && bytes.length > 0) {
                    providerInfo.setMetadata(new String(bytes));
                }
                result.add(providerInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<InvokerInfo> getInvokerInfosByServiceNameAndClassInfo(String serviceName, ClassInfo classInfo) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(addresses)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
    }
}