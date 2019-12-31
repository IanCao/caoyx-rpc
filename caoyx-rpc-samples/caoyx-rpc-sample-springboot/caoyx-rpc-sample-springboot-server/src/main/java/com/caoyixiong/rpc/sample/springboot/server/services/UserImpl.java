package com.caoyixiong.rpc.sample.springboot.server.services;

import com.caoyixiong.rpc.sample.springboot.api.IUser;
import com.caoyixiong.rpc.sample.springboot.api.UserDto;
import com.caoyixiong.rpc.sample.springboot.server.exception.CaoyxRpcProviderException;
import com.caoyx.rpc.spring.provider.CaoyxRpcService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-26 15:30
 */
@Service
@CaoyxRpcService
public class UserImpl implements IUser {

    private List<UserDto> userDtos = new ArrayList<UserDto>();

    public boolean addUser(UserDto userDto)  {
        if (userDto.getName().equals("aaaa")) {
            throw new CaoyxRpcProviderException("name is valid");
        }
        return userDtos.add(userDto);
    }

    public List<UserDto> getUsers() {
        return userDtos;
    }
}