package com.caoyx.rpc.sample.simple.api;

import java.util.List;

/**
 * @author caoyixiong
 */
public interface IUser {
    boolean addUser(UserDto userDto);

    List<UserDto> getUsers();

    void addUserVoid(UserDto userDto);
}