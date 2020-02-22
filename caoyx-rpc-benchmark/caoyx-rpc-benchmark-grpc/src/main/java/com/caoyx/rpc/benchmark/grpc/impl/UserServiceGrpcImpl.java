package com.caoyx.rpc.benchmark.grpc.impl;

import com.caoyx.rpc.benchmark.base.Page;
import com.caoyx.rpc.benchmark.base.UserService;
import com.caoyx.rpc.benchmark.base.UserServiceImpl;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceGrpc.UserServiceImplBase;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.CreateUserResponse;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.GetUserRequest;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.ListUserRequest;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.User;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.UserExistRequest;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.UserExistResponse;
import com.caoyx.rpc.benchmark.proto.grpc.UserServiceOuterClass.UserPage;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-22 20:52
 */
public class UserServiceGrpcImpl extends UserServiceImplBase {
    private final UserService userService = new UserServiceImpl();

    @Override
    public void userExist(UserExistRequest request, StreamObserver<UserExistResponse> responseObserver) {
        String email = request.getEmail();
        boolean isExist = userService.existUser(email);

        UserExistResponse reply = UserExistResponse.newBuilder().setExist(isExist).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
        long id = request.getId();
        com.caoyx.rpc.benchmark.base.User user = userService.getUser(id);

        User reply = User.newBuilder()
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

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    public void createUser(User request, StreamObserver<CreateUserResponse> responseObserver) {
        com.caoyx.rpc.benchmark.base.User user = new com.caoyx.rpc.benchmark.base.User();

        user.setId(request.getId());
        user.setName(request.getName());
        user.setSex(request.getSex());
        user.setBirthday(request.getBirthday());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setAddress(request.getAddress());
        user.setIcon(request.getIcon());
        user.setPermissions(request.getPermissionsList());
        user.setStatus(request.getStatus());
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());

        boolean success = userService.createUser(user);

        CreateUserResponse reply = CreateUserResponse.newBuilder().setSuccess(success).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void listUser(ListUserRequest request, StreamObserver<UserPage> responseObserver) {
        int pageNo = request.getPageNo();

        Page<com.caoyx.rpc.benchmark.base.User> page = userService.listUser(pageNo);

        List<User> userList = new ArrayList<>(page.getResult().size());

        for (com.caoyx.rpc.benchmark.base.User user : page.getResult()) {
            User u = User.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setSex(user.getSex())
                    .setBirthday(user.getBirthday())
                    .setEmail(user.getEmail())
                    .setMobile(user.getMobile())
                    .setAddress(user.getAddress())
                    .setIcon(user.getIcon())
                    .addAllPermissions(user.getPermissions())
                    .setStatus(user.getStatus())
                    .setCreateTime(user.getCreateTime())
                    .setUpdateTime(user.getUpdateTime())
                    .build();

            userList.add(u);
        }

        UserPage reply = UserPage.newBuilder()
                .setPageNo(page.getPageNo())
                .setTotal(page.getTotal())
                .addAllResult(userList)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}