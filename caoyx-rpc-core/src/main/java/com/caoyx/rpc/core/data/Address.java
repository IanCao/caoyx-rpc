package com.caoyx.rpc.core.data;

import lombok.Data;

import java.util.Objects;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 12:59
 */
@Data
public class Address {

    private String ip;

    private int port;

    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return port == address.port &&
                Objects.equals(ip, address.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}