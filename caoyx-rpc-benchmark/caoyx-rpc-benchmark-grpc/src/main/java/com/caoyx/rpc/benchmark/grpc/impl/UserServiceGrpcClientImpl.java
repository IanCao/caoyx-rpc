package com.caoyx.rpc.benchmark.grpc.impl;

import com.caoyx.rpc.benchmark.base.Page;
import com.caoyx.rpc.benchmark.base.User;
import com.caoyx.rpc.benchmark.base.UserService;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceGrpc;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.CreateUserResponse;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.GetUserRequest;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.ListUserRequest;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.UserExistRequest;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.UserExistResponse;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.UserPage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserServiceGrpcClientImpl implements UserService, Closeable {

	private final ManagedChannel channel;
	private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

	public UserServiceGrpcClientImpl() {
		ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", 8080).usePlaintext(true);
		channel = channelBuilder.build();
		userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
	}

	@Override
	public void close() throws IOException {
		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean existUser(String email) {
		UserExistRequest request = UserExistRequest.newBuilder().setEmail(email).build();
		UserExistResponse response = userServiceBlockingStub.userExist(request);

		return response.getExist();
	}

	@Override
	public boolean createUser(User user) {
		UserServiceOuterClass.User request = UserServiceOuterClass.User
				.newBuilder()
				.setId(user.getId())
				.setName(user.getName())
				.setSex(user.getSex())
				.setBirthday(System.currentTimeMillis())
				.setEmail(user.getEmail())
				.setMobile(user.getMobile())
				.setAddress(user.getAddress())
				.setIcon(user.getIcon())
				.addAllPermissions(user.getPermissions())
				.setStatus(user.getStatus())
				.setCreateTime(System.currentTimeMillis())
				.setUpdateTime(System.currentTimeMillis())
				.build();

		CreateUserResponse response = userServiceBlockingStub.createUser(request);

		return response.getSuccess();
	}

	@Override
	public User getUser(long id) {
		GetUserRequest request = GetUserRequest.newBuilder().setId(id).build();
		UserServiceOuterClass.User response = userServiceBlockingStub.getUser(request);

		User user = new User();
		user.setId(response.getId());
		user.setName(response.getName());
		user.setSex(response.getSex());
		user.setBirthday(response.getBirthday());
		user.setEmail(response.getEmail());
		user.setMobile(response.getMobile());
		user.setAddress(response.getAddress());
		user.setIcon(response.getIcon());
		user.setPermissions(response.getPermissionsList());
		user.setStatus(response.getStatus());
		user.setCreateTime(response.getCreateTime());
		user.setUpdateTime(response.getUpdateTime());

		return user;
	}

	@Override
	public Page<User> listUser(int pageNo) {
		ListUserRequest request = ListUserRequest.newBuilder().setPageNo(pageNo).build();
		UserPage response = userServiceBlockingStub.listUser(request);

		Page<User> page = new Page<>();

		page.setPageNo(response.getPageNo());
		page.setTotal(response.getTotal());

		List<User> userList = new ArrayList<>(response.getResultCount());

		for (UserServiceOuterClass.User u : response.getResultList()) {
			User user = new User();
			user.setId(u.getId());
			user.setName(u.getName());
			user.setSex(u.getSex());
			user.setBirthday(u.getBirthday());
			user.setEmail(u.getEmail());
			user.setMobile(u.getMobile());
			user.setAddress(u.getAddress());
			user.setIcon(u.getIcon());
			user.setPermissions(u.getPermissionsList());
			user.setStatus(u.getStatus());
			user.setCreateTime(u.getCreateTime());
			user.setUpdateTime(u.getUpdateTime());

			userList.add(user);
		}

		page.setResult(userList);

		return page;
	}

}
