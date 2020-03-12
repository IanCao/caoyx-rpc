package com.caoyx.rpc.core.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassKey classKey = (ClassKey) o;
        return Objects.equals(className, classKey.className) &&
                Objects.equals(version, classKey.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, version);
    }
}