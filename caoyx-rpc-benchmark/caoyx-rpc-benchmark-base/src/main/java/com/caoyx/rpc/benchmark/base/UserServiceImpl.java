package com.caoyx.rpc.benchmark.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:28
 */
public class UserServiceImpl implements UserService {
    @Override
    public boolean existUser(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        if (email.charAt(email.length() - 1) < '5') {
            return false;
        }
        return true;

    }

    @Override
    public boolean createUser(User user) {
        if (user == null) {
            return false;
        }
        return true;
    }

    @Override
    public User getUser(long id) {
        return initUser(id);
    }

    @Override
    public Page<User> listUser(int pageNo) {
        List<User> userList = new ArrayList<>(15);

        for (int i = 0; i < 1; i++) {
            userList.add(initUser(i));
        }
        Page<User> page = new Page<>();
        page.setPageNo(pageNo);
        page.setTotal(1000);
        page.setResult(userList);
        return page;
    }

    private User initUser(long id) {
        User user = new User();

        user.setId(id);
        user.setName("IanCao");
        user.setSex(1);
        user.setBirthday(new Date(1994, 12, 20).getTime());
        user.setEmail("caoyixiong@apache.org");
        user.setMobile("1234567890");
        user.setAddress("北京市 回龙观 程序员之家");
        user.setIcon("https://www.baidu.com/img/bd_logo1.png");
        user.setStatus(1);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());

        List<Integer> permissions = new ArrayList<Integer>(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 19, 88, 86, 89, 90, 91, 92));
        user.setPermissions(permissions);

        return user;
    }
}