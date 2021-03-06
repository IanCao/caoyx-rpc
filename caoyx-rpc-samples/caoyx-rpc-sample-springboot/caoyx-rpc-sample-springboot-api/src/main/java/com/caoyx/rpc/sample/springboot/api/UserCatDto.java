package com.caoyx.rpc.sample.springboot.api;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author caoyixiong
 */
@Data
@Accessors(chain = true)
public class UserCatDto implements Serializable {
    private String name;
}