package com.caoyx.rpc.core.register;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @Author: caoyixiong
 * @Date: 2019-12-31 12:38
 */
@Data
@Accessors(chain = true)
public class RegisterConfig {
    private String address;
    private RegisterType registerType;


    public RegisterConfig(String address, RegisterType registerType) {
        this.address = address;
        this.registerType = registerType;
    }
}