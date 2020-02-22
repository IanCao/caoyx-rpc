package com.caoyx.rpc.benchmark.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:24
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {
    private long id;
    private String name;
    private int sex;
    private long birthday;
    private String email;
    private String mobile;
    private String address;
    private String icon;
    private List<Integer> permissions;
    private int status;
    private long createTime;
    private long updateTime;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", icon='" + icon + '\'' +
                ", permissions=" + permissions +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}