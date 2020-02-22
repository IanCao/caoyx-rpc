package com.caoyx.rpc.benchmark.grpc;

import com.caoyx.rpc.benchmark.base.Page;
import com.caoyx.rpc.benchmark.base.User;
import com.caoyx.rpc.benchmark.base.UserServiceImpl;
import com.caoyx.rpc.benchmark.grpc.impl.UserServiceGrpcClientImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:40
 */
@State(Scope.Benchmark)
public class GrpcBenchmark {

    public static final int CONCURRENCY = 32;

    private final UserServiceGrpcClientImpl userService = new UserServiceGrpcClientImpl();
    private final UserServiceImpl userServiceImpl = new UserServiceImpl();
    private final AtomicInteger counter = new AtomicInteger(0);


    @TearDown
    public void close() throws IOException {
        userService.close();
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean existUser() throws Exception {
        return userService.existUser(counter.getAndIncrement() + "");
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean createUser() throws Exception {
        User user = userServiceImpl.getUser(counter.incrementAndGet());
        return userService.createUser(user);
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public User getUser() throws Exception {
        return userService.getUser(counter.getAndIncrement());
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Page<User> listUser() throws Exception {
        return userService.listUser(counter.getAndIncrement());
    }

    public static void main(String[] args) throws Exception {
        GrpcBenchmark grpcBenchmark = new GrpcBenchmark();

        for (int i = 0; i < 60; i++) {
            try {
                System.out.println(grpcBenchmark.getUser());
                break;
            } catch (Exception e) {
                Thread.sleep(1000);
            }
        }

        grpcBenchmark.close();

        Options opt = new OptionsBuilder()
                .include(GrpcBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(10))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(10))
                .threads(CONCURRENCY)
                .forks(1)//
                .build();

        new Runner(opt).run();
    }

}