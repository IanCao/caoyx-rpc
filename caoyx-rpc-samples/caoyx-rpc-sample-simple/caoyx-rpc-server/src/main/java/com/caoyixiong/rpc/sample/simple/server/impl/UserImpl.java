package com.caoyixiong.rpc.sample.simple.server.impl;

import com.caoyixiong.rpc.sample.simple.api.IUser;
import com.caoyixiong.rpc.sample.simple.api.UserDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caoyixiong
 */
public class UserImpl implements IUser {

    List<UserDto> userDtos = new ArrayList<UserDto>();

    public boolean addUser(UserDto userDto) {
        return userDtos.add(userDto);
    }

    public List<UserDto> getUsers() {
        return userDtos;
    }

    @Override
    public void addUserVoid(UserDto userDto) {
        userDtos.add(userDto);
    }
}