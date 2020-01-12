package com.caoyx.rpc.core.register;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 12:38
 */
@Data
@Accessors(chain = true)
public class RegisterConfig {
    private String registerAddress;
    private String registerName;
    private List<String> loadAddresses;


    public RegisterConfig(String registerAddress, String registerName) {
        this.registerAddress = registerAddress;
        this.registerName = registerName;
    }

    public RegisterConfig(String registerName, String registerAddress, List<String> loadAddresses) {
        this.registerAddress = registerAddress;
        this.registerName = registerName;
        this.loadAddresses = loadAddresses;
    }
}