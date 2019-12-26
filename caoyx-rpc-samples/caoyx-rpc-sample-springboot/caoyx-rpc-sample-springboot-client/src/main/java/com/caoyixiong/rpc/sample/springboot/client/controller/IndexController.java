package com.caoyixiong.rpc.sample.springboot.client.controller;


import com.caoyixiong.rpc.sample.springboot.api.IUser;
import com.caoyixiong.rpc.sample.springboot.api.UserDto;
import com.caoyx.rpc.core.spring.invoker.CaoyxRpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class IndexController {

    @CaoyxRpcReference(addressList = {"127.0.0.1:1118"})
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
