package com.caoyx.rpc.sample.springboot.client.controller;


import com.caoyx.rpc.sample.springboot.api.IUser;
import com.caoyx.rpc.sample.springboot.api.UserDto;
import com.caoyx.rpc.spring.invoker.CaoyxRpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @CaoyxRpcReference(providerApplicationName = "caoyxRpc-sample-springboot-server")
    private IUser user;

    @CaoyxRpcReference(providerApplicationName = "caoyxRpc-sample-springboot-server")
    private IUser user1;

    @GetMapping("/addDefaultUser")
    public boolean addDefaultUser(String name, int age) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setAge(age);
        return user.addUser(userDto);
    }

    @GetMapping("/getUsers")
    public List<UserDto> getUsers() {
        return user1.getUsers();
    }
}
