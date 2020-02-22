package com.caoyx.rpc.core.data;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-21 16:26
 */
@Data
@Accessors(chain = true)
public class ClassKey {
    private String className;
    private Integer version;

    public ClassKey(String className, Integer version) {
        this.className = className;
        this.version = version;
    }

    @Override
    public String toString() {
        return className + "@" + version;
    }
}