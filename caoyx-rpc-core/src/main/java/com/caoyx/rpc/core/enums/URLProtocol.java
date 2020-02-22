package com.caoyx.rpc.core.enums;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-21 22:46
 */
public enum URLProtocol {
    PROVIDER("provider"),
    INVOKER("invoker");

    private String label;

    URLProtocol(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static URLProtocol getURLProtocolByLabel(String label) {
        URLProtocol[] urlProtocols = values();
        for (URLProtocol urlProtocol : urlProtocols) {
            if (urlProtocol.label.equals(label)) {
                return urlProtocol;
            }
        }
        return null;
    }
}