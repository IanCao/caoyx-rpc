package com.caoyx.rpc.benchmark.grpc;


import com.caoyx.rpc.benchmark.grpc.impl.UserServiceGrpcImpl;
import io.grpc.ServerBuilder;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:40
 */
public class Server {
    public static void main(String[] args) throws Exception {
        ServerBuilder//
                .forPort(8080)
                .addService(new UserServiceGrpcImpl())
                .build()
                .start()
                .awaitTermination();
    }
}