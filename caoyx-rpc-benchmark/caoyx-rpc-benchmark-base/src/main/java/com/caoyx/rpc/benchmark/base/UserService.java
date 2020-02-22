package com.caoyx.rpc.benchmark.base;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:24
 */
public interface UserService {
    boolean existUser(String email);

    boolean createUser(User user);

    User getUser(long id);

    Page<User> listUser(int pageNo);
}