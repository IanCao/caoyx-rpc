package com.caoyx.rpc.core.url.register;

import com.caoyx.rpc.core.url.URL;
import lombok.Data;

import java.util.Map;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-21 23:47
 */
@Data
public class RegisterURL extends URL {
    private String host;
    private int port;
    private String applicationName;
    private Map<String, String> metadata;

    private String className;
    private int implVersion;

    public String getClassKey() {
        return className + "_" + implVersion;
    }

    public String getHostPort() {
        return host + ":" + port;
    }
}