package com.caoyx.rpc.core.url.register;

import com.caoyx.rpc.core.enums.URLProtocol;
import lombok.Data;


/**
 * @Author: caoyixiong
 * @Date: 2020-02-21 23:11
 */
@Data
public class ProviderURL extends RegisterURL {
    private boolean inValid;
    private int weight;

    public ProviderURL() {
        setProtocol(URLProtocol.PROVIDER);
    }
}