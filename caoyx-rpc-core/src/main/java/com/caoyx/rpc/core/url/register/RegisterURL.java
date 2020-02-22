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
    private String hostPort;
    private String applicationName;
    private Map<String, Object> metadata;

    private String className;
    private int implVersion;

    public String getClassKey() {
        return className + "@" + implVersion;
    }
}