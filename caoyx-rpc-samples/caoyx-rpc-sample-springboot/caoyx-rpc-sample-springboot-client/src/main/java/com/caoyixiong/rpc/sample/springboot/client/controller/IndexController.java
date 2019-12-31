package com.caoyixiong.rpc.sample.springboot.client.controller;


import com.caoyixiong.rpc.sample.springboot.api.IUser;
import com.caoyixiong.rpc.sample.springboot.api.UserDto;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.spring.invoker.CaoyxRpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @CaoyxRpcReference(callType = CallType.REGISTER,
            registerAddress = "127.0.0.1:2181",
            remoteApplicationName = "caoyxRpc-sample-springboot-server",
            retryTimes = 2)
    private IUser user;

    @GetMapping("/addDefaultUser")
    public boolean addDefaultUser(String name, int age) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setAge(age);
        return user.addUser(userDto);
    }

    @GetMapping("/getUsers")
    public List<UserDto> getUsers() {
        return user.getUsers();
    }
}
