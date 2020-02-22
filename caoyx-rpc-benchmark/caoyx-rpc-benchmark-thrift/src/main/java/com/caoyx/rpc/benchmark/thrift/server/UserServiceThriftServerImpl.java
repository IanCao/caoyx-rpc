package com.caoyx.rpc.benchmark.thrift.server;

import com.caoyx.rpc.benchmark.base.Page;
import com.caoyx.rpc.benchmark.base.UserServiceImpl;
import com.caoyx.rpc.benchmark.thrift.Converter;
import com.caoyx.rpc.benchmark.thrift.impl.User;
import com.caoyx.rpc.benchmark.thrift.impl.UserPage;
import com.caoyx.rpc.benchmark.thrift.impl.UserService;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class UserServiceThriftServerImpl implements UserService.Iface {

	private final com.caoyx.rpc.benchmark.base.UserService userService = new UserServiceImpl();

	@Override
	public boolean userExist(String email) throws TException {
		return userService.existUser(email);
	}

	@Override
	public boolean createUser(User user) throws TException {
		com.caoyx.rpc.benchmark.base.User rawUser = Converter.toRaw(user);
		return userService.createUser(rawUser);
	}

	@Override
	public User getUser(long id) throws TException {
		com.caoyx.rpc.benchmark.base.User rawUser = userService.getUser(id);
		User user = Converter.toThrift(rawUser);
		return user;
	}

	@Override
	public UserPage listUser(int pageNo) throws TException {
		Page<com.caoyx.rpc.benchmark.base.User> page = userService.listUser(pageNo);

		List<User> userList = new ArrayList<>();
		for (com.caoyx.rpc.benchmark.base.User rawUser : page.getResult()) {
			User user = Converter.toThrift(rawUser);
			userList.add(user);
		}

		UserPage userPage = new UserPage();
		userPage.setPageNo(page.getPageNo());
		userPage.setTotal(page.getTotal());
		userPage.setResult(userList);

		return userPage;
	}

}
