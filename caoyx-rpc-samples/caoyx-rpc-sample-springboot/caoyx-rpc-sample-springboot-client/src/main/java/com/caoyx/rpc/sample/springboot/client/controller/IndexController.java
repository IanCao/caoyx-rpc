package com.caoyx.rpc.sample.springboot.client.controller;


import com.caoyx.rpc.core.serialization.api.SerializerAlgorithm;
import com.caoyx.rpc.sample.springboot.api.IUser;
import com.caoyx.rpc.sample.springboot.api.UserDto;
import com.caoyx.rpc.spring.invoker.CaoyxRpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @CaoyxRpcReference(loadAddress = {"127.0.0.1:1118"},
            remoteApplicationName = "caoyxRpc-sample-springboot-server",
            serializer = SerializerAlgorithm.JDK,
            retryTimes = 2)
    private IUser user;

    @CaoyxRpcReference(loadAddress = {"127.0.0.1:1118"},
            remoteApplicationName = "caoyxRpc-sample-springboot-server",
            serializer = SerializerAlgorithm.HESSIAN2,
            retryTimes = 2)
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
