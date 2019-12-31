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
    private String registerAddress;
    private Register register;

    public RegisterConfig(Register register, String registerAddress) {
        this.registerAddress = registerAddress;
        this.register = register;
    }
}