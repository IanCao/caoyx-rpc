package com.caoyx.rpc.core.url.register;

import com.caoyx.rpc.core.enums.URLProtocol;
import lombok.Data;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-21 23:11
 */
@Data
public class InvokerURL extends RegisterURL {

    private String providerApplicationName;

    public InvokerURL() {
        setProtocol(URLProtocol.INVOKER);
    }
}