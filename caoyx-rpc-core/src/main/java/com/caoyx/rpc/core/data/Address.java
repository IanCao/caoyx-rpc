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

    public Address(String ipPort) {
        String[] strings = ipPort.split(":");
        this.ip = strings[0];
        this.port = Integer.valueOf(strings[1]);
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

    @Override
    public String toString() {
        return "Address{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}