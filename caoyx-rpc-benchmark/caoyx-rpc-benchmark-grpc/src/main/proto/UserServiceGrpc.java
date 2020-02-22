package com.caoyx.rpc.benchmark.proto.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.18.0)",
    comments = "Source: UserService.proto")
public final class UserServiceGrpc {

  private UserServiceGrpc() {}

  public static final String SERVICE_NAME = "grpc.UserService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<UserServiceOuterClass.UserExistRequest,
      UserServiceOuterClass.UserExistResponse> getUserExistMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "userExist",
      requestType = UserServiceOuterClass.UserExistRequest.class,
      responseType = UserServiceOuterClass.UserExistResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<UserServiceOuterClass.UserExistRequest,
      UserServiceOuterClass.UserExistResponse> getUserExistMethod() {
    io.grpc.MethodDescriptor<UserServiceOuterClass.UserExistRequest, UserServiceOuterClass.UserExistResponse> getUserExistMethod;
    if ((getUserExistMethod = UserServiceGrpc.getUserExistMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getUserExistMethod = UserServiceGrpc.getUserExistMethod) == null) {
          UserServiceGrpc.getUserExistMethod = getUserExistMethod = 
              io.grpc.MethodDescriptor.<UserServiceOuterClass.UserExistRequest, UserServiceOuterClass.UserExistResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "grpc.UserService", "userExist"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.UserExistRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.UserExistResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("userExist"))
                  .build();
          }
        }
     }
     return getUserExistMethod;
  }

  private static volatile io.grpc.MethodDescriptor<UserServiceOuterClass.User,
      UserServiceOuterClass.CreateUserResponse> getCreateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createUser",
      requestType = UserServiceOuterClass.User.class,
      responseType = UserServiceOuterClass.CreateUserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<UserServiceOuterClass.User,
      UserServiceOuterClass.CreateUserResponse> getCreateUserMethod() {
    io.grpc.MethodDescriptor<UserServiceOuterClass.User, UserServiceOuterClass.CreateUserResponse> getCreateUserMethod;
    if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
          UserServiceGrpc.getCreateUserMethod = getCreateUserMethod = 
              io.grpc.MethodDescriptor.<UserServiceOuterClass.User, UserServiceOuterClass.CreateUserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "grpc.UserService", "createUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.User.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.CreateUserResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("createUser"))
                  .build();
          }
        }
     }
     return getCreateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<UserServiceOuterClass.GetUserRequest,
      UserServiceOuterClass.User> getGetUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getUser",
      requestType = UserServiceOuterClass.GetUserRequest.class,
      responseType = UserServiceOuterClass.User.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<UserServiceOuterClass.GetUserRequest,
      UserServiceOuterClass.User> getGetUserMethod() {
    io.grpc.MethodDescriptor<UserServiceOuterClass.GetUserRequest, UserServiceOuterClass.User> getGetUserMethod;
    if ((getGetUserMethod = UserServiceGrpc.getGetUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserMethod = UserServiceGrpc.getGetUserMethod) == null) {
          UserServiceGrpc.getGetUserMethod = getGetUserMethod = 
              io.grpc.MethodDescriptor.<UserServiceOuterClass.GetUserRequest, UserServiceOuterClass.User>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "grpc.UserService", "getUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.GetUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.User.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("getUser"))
                  .build();
          }
        }
     }
     return getGetUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<UserServiceOuterClass.ListUserRequest,
      UserServiceOuterClass.UserPage> getListUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "listUser",
      requestType = UserServiceOuterClass.ListUserRequest.class,
      responseType = UserServiceOuterClass.UserPage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<UserServiceOuterClass.ListUserRequest,
      UserServiceOuterClass.UserPage> getListUserMethod() {
    io.grpc.MethodDescriptor<UserServiceOuterClass.ListUserRequest, UserServiceOuterClass.UserPage> getListUserMethod;
    if ((getListUserMethod = UserServiceGrpc.getListUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getListUserMethod = UserServiceGrpc.getListUserMethod) == null) {
          UserServiceGrpc.getListUserMethod = getListUserMethod = 
              io.grpc.MethodDescriptor.<UserServiceOuterClass.ListUserRequest, UserServiceOuterClass.UserPage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "grpc.UserService", "listUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.ListUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UserServiceOuterClass.UserPage.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("listUser"))
                  .build();
          }
        }
     }
     return getListUserMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserServiceStub newStub(io.grpc.Channel channel) {
    return new UserServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new UserServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new UserServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class UserServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void userExist(UserServiceOuterClass.UserExistRequest request,
                          io.grpc.stub.StreamObserver<UserServiceOuterClass.UserExistResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getUserExistMethod(), responseObserver);
    }

    /**
     */
    public void createUser(UserServiceOuterClass.User request,
                           io.grpc.stub.StreamObserver<UserServiceOuterClass.CreateUserResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateUserMethod(), responseObserver);
    }

    /**
     */
    public void getUser(UserServiceOuterClass.GetUserRequest request,
                        io.grpc.stub.StreamObserver<UserServiceOuterClass.User> responseObserver) {
      asyncUnimplementedUnaryCall(getGetUserMethod(), responseObserver);
    }

    /**
     */
    public void listUser(UserServiceOuterClass.ListUserRequest request,
                         io.grpc.stub.StreamObserver<UserServiceOuterClass.UserPage> responseObserver) {
      asyncUnimplementedUnaryCall(getListUserMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getUserExistMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                UserServiceOuterClass.UserExistRequest,
                UserServiceOuterClass.UserExistResponse>(
                  this, METHODID_USER_EXIST)))
          .addMethod(
            getCreateUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                UserServiceOuterClass.User,
                UserServiceOuterClass.CreateUserResponse>(
                  this, METHODID_CREATE_USER)))
          .addMethod(
            getGetUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                UserServiceOuterClass.GetUserRequest,
                UserServiceOuterClass.User>(
                  this, METHODID_GET_USER)))
          .addMethod(
            getListUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                UserServiceOuterClass.ListUserRequest,
                UserServiceOuterClass.UserPage>(
                  this, METHODID_LIST_USER)))
          .build();
    }
  }

  /**
   */
  public static final class UserServiceStub extends io.grpc.stub.AbstractStub<UserServiceStub> {
    private UserServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected UserServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserServiceStub(channel, callOptions);
    }

    /**
     */
    public void userExist(UserServiceOuterClass.UserExistRequest request,
                          io.grpc.stub.StreamObserver<UserServiceOuterClass.UserExistResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUserExistMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createUser(UserServiceOuterClass.User request,
                           io.grpc.stub.StreamObserver<UserServiceOuterClass.CreateUserResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getUser(UserServiceOuterClass.GetUserRequest request,
                        io.grpc.stub.StreamObserver<UserServiceOuterClass.User> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listUser(UserServiceOuterClass.ListUserRequest request,
                         io.grpc.stub.StreamObserver<UserServiceOuterClass.UserPage> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListUserMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class UserServiceBlockingStub extends io.grpc.stub.AbstractStub<UserServiceBlockingStub> {
    private UserServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected UserServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public UserServiceOuterClass.UserExistResponse userExist(UserServiceOuterClass.UserExistRequest request) {
      return blockingUnaryCall(
          getChannel(), getUserExistMethod(), getCallOptions(), request);
    }

    /**
     */
    public UserServiceOuterClass.CreateUserResponse createUser(UserServiceOuterClass.User request) {
      return blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public UserServiceOuterClass.User getUser(UserServiceOuterClass.GetUserRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public UserServiceOuterClass.UserPage listUser(UserServiceOuterClass.ListUserRequest request) {
      return blockingUnaryCall(
          getChannel(), getListUserMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class UserServiceFutureStub extends io.grpc.stub.AbstractStub<UserServiceFutureStub> {
    private UserServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected UserServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<UserServiceOuterClass.UserExistResponse> userExist(
        UserServiceOuterClass.UserExistRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUserExistMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<UserServiceOuterClass.CreateUserResponse> createUser(
        UserServiceOuterClass.User request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<UserServiceOuterClass.User> getUser(
        UserServiceOuterClass.GetUserRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<UserServiceOuterClass.UserPage> listUser(
        UserServiceOuterClass.ListUserRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListUserMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_USER_EXIST = 0;
  private static final int METHODID_CREATE_USER = 1;
  private static final int METHODID_GET_USER = 2;
  private static final int METHODID_LIST_USER = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UserServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UserServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_USER_EXIST:
          serviceImpl.userExist((UserServiceOuterClass.UserExistRequest) request,
              (io.grpc.stub.StreamObserver<UserServiceOuterClass.UserExistResponse>) responseObserver);
          break;
        case METHODID_CREATE_USER:
          serviceImpl.createUser((UserServiceOuterClass.User) request,
              (io.grpc.stub.StreamObserver<UserServiceOuterClass.CreateUserResponse>) responseObserver);
          break;
        case METHODID_GET_USER:
          serviceImpl.getUser((UserServiceOuterClass.GetUserRequest) request,
              (io.grpc.stub.StreamObserver<UserServiceOuterClass.User>) responseObserver);
          break;
        case METHODID_LIST_USER:
          serviceImpl.listUser((UserServiceOuterClass.ListUserRequest) request,
              (io.grpc.stub.StreamObserver<UserServiceOuterClass.UserPage>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return UserServiceOuterClass.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserService");
    }
  }

  private static final class UserServiceFileDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier {
    UserServiceFileDescriptorSupplier() {}
  }

  private static final class UserServiceMethodDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    UserServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UserServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserServiceFileDescriptorSupplier())
              .addMethod(getUserExistMethod())
              .addMethod(getCreateUserMethod())
              .addMethod(getGetUserMethod())
              .addMethod(getListUserMethod())
              .build();
        }
      }
    }
    return result;
  }
}
