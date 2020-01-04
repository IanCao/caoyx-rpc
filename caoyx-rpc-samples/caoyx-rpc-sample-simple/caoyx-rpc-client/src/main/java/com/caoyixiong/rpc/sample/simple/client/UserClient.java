package com.caoyixiong.rpc.sample.simple.client;

import com.caoyixiong.rpc.sample.simple.api.IUser;
import com.caoyixiong.rpc.sample.simple.api.UserCatDto;
import com.caoyixiong.rpc.sample.simple.api.UserDto;
import com.caoyx.rpc.core.netty.client.NettyClient;
import com.caoyx.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.caoyx.rpc.core.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.serializer.SerializerAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caoyixiong
 */
public class UserClient {

    public static void main(String[] args) throws Exception {
        List<String> loadAddresses = new ArrayList<String>();
        loadAddresses.add("127.0.0.1:1118");
        CaoyxRpcReferenceBean rpcReferenceBean = new CaoyxRpcReferenceBean(IUser.class,
                "0",
                "caoyxRpc",
                new RegisterConfig(
                        "noRegister",
                        "",
                        loadAddresses),
                NettyClient.class,
                SerializerAlgorithm.JDK);
        rpcReferenceBean.setLoadBalance(new RandomLoadBalance());
        rpcReferenceBean.init();

        IUser user = (IUser) rpcReferenceBean.getObject();

        UserDto userDto = new UserDto();
        userDto.setName("test1-1");
        userDto.setAge(1);
        List<String> hobbies = new ArrayList<String>();
        hobbies.add("test1-hobby-1");
        hobbies.add("test1-hobby-2");
        hobbies.add("test1-hobby-3");
        hobbies.add("test1-hobby-4");
        userDto.setHobbies(hobbies);

        List<UserCatDto> userCatDtos = new ArrayList<UserCatDto>();
        userCatDtos.add(new UserCatDto().setName("test1-cat-name-1"));
        userCatDtos.add(new UserCatDto().setName("test1-cat-name-2"));
        userCatDtos.add(new UserCatDto().setName("test1-cat-name-3"));
        userDto.setUserCatDtos(userCatDtos);
        userDto.setAddress("BeiJing");

        user.addUser(userDto);

        System.out.println(user.getUsers().toString());
    }
}