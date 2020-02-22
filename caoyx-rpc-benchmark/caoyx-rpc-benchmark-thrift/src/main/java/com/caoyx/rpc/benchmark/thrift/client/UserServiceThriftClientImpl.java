package com.caoyx.rpc.benchmark.thrift.client;

import com.caoyx.rpc.benchmark.base.Page;
import com.caoyx.rpc.benchmark.base.User;
import com.caoyx.rpc.benchmark.base.UserService;
import com.caoyx.rpc.benchmark.thrift.Converter;
import com.caoyx.rpc.benchmark.thrift.impl.UserPage;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserServiceThriftClientImpl implements UserService, Closeable {


	private final LockObjectPool<ThriftUserServiceClient> clientPool = //
			new LockObjectPool<>(32, () -> new ThriftUserServiceClient("127.0.0.1", 8080));

	@Override
	public void close() throws IOException {
		clientPool.close();
	}

	@Override
	public boolean existUser(String email) {
		ThriftUserServiceClient thriftUserServiceClient = clientPool.borrow();
		try {
			return thriftUserServiceClient.client.userExist(email);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			clientPool.release(thriftUserServiceClient);
		}
	}

	@Override
	public boolean createUser(User user) {
		com.caoyx.rpc.benchmark.thrift.impl.User thriftUser = Converter.toThrift(user);

		ThriftUserServiceClient thriftUserServiceClient = clientPool.borrow();
		try {
			return thriftUserServiceClient.client.createUser(thriftUser);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			clientPool.release(thriftUserServiceClient);
		}
	}

	@Override
	public User getUser(long id) {

		ThriftUserServiceClient thriftUserServiceClient = clientPool.borrow();
		try {

			com.caoyx.rpc.benchmark.thrift.impl.User thriftUser = thriftUserServiceClient.client.getUser(id);
			User user = Converter.toRaw(thriftUser);

			return user;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			clientPool.release(thriftUserServiceClient);
		}
	}

	@Override
	public Page<User> listUser(int pageNo) {

		ThriftUserServiceClient thriftUserServiceClient = clientPool.borrow();
		try {

			UserPage userPage = thriftUserServiceClient.client.listUser(pageNo);
			Page<User> page = new Page<>();

			page.setPageNo(userPage.getPageNo());
			page.setTotal(userPage.getTotal());

			List<User> userList = new ArrayList<>(userPage.getResult().size());

			for (com.caoyx.rpc.benchmark.thrift.impl.User thriftUser : userPage.getResult()) {
				User user = Converter.toRaw(thriftUser);

				userList.add(user);
			}

			page.setResult(userList);

			return page;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			clientPool.release(thriftUserServiceClient);
		}

	}
}
