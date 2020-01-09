package com.caoyx.rpc.sample.simple.api;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author caoyixiong
 */
@Data
public class UserDto implements Serializable {
    private String name;
    private int age;
    private String address;
    private List<String> hobbies;
    private List<UserCatDto> userCatDtos;

    public UserDto(String name) {
        this.name = name;
    }
}


