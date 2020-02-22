package com.caoyx.rpc.benchmark.thrift;

import com.caoyx.rpc.benchmark.thrift.impl.User;

public class Converter {

	public static User toThrift(com.caoyx.rpc.benchmark.base.User user) {
		User thriftUser = new User();

		thriftUser.setId(user.getId());
		thriftUser.setName(user.getName());
		thriftUser.setSex(user.getSex());
		thriftUser.setBirthday(user.getBirthday());
		thriftUser.setEmail(user.getEmail());
		thriftUser.setMobile(user.getMobile());
		thriftUser.setAddress(user.getAddress());
		thriftUser.setIcon(user.getIcon());
		thriftUser.setPermissions(user.getPermissions());
		thriftUser.setStatus(user.getStatus());
		thriftUser.setCreateTime(user.getCreateTime());
		thriftUser.setUpdateTime(user.getUpdateTime());

		return thriftUser;
	}

	public static com.caoyx.rpc.benchmark.base.User toRaw(User user) {
		com.caoyx.rpc.benchmark.base.User rawUser = new com.caoyx.rpc.benchmark.base.User();

		rawUser.setId(user.getId());
		rawUser.setName(user.getName());
		rawUser.setSex(user.getSex());
		rawUser.setBirthday(user.getBirthday());
		rawUser.setEmail(user.getEmail());
		rawUser.setMobile(user.getMobile());
		rawUser.setAddress(user.getAddress());
		rawUser.setIcon(user.getIcon());
		rawUser.setPermissions(user.getPermissions());
		rawUser.setStatus(user.getStatus());
		rawUser.setCreateTime(user.getCreateTime());
		rawUser.setUpdateTime(user.getUpdateTime());

		return rawUser;
	}
}
