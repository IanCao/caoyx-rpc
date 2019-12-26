package com.caoyixiong.rpc.sample.springboot.api;
import java.util.List;

/**
 * @author caoyixiong
 */
public interface IUser {
    boolean addUser(UserDto userDto);

    List<UserDto> getUsers();
}