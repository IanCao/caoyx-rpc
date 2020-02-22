package com.caoyx.rpc.benchmark.thrift;

import com.caoyx.rpc.benchmark.base.Page;
import com.caoyx.rpc.benchmark.base.User;
import com.caoyx.rpc.benchmark.base.UserServiceImpl;
import com.caoyx.rpc.benchmark.thrift.client.UserServiceThriftClientImpl;
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
 * @Date: 2020-02-19 23:41
 */
@State(Scope.Benchmark)
public class ThriftBenchmark {
    public static final int CONCURRENCY = 32;

    private final AtomicInteger counter = new AtomicInteger(0);

    private final UserServiceThriftClientImpl userServiceThriftClient = new UserServiceThriftClientImpl();
    private final UserServiceImpl userServiceImpl = new UserServiceImpl();

    @TearDown
    public void close() throws IOException {
        userServiceThriftClient.close();
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean existUser() throws Exception {
        return userServiceThriftClient.existUser(counter.getAndIncrement() + "");
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean createUser() throws Exception {
        User user = userServiceImpl.getUser(counter.incrementAndGet());
        return userServiceThriftClient.createUser(user);
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public User getUser() throws Exception {
        return userServiceThriftClient.getUser(counter.getAndIncrement());
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Page<User> listUser() throws Exception {
        return userServiceThriftClient.listUser(counter.getAndIncrement());
    }

    public static void main(String[] args) throws Exception {
        ThriftBenchmark client = new ThriftBenchmark();

        for (int i = 0; i < 60; i++) {
            try {
                System.out.println(client.getUser());
                break;
            } catch (Exception e) {
                Thread.sleep(1000);
            }
        }

        client.close();

        Options opt = new OptionsBuilder()//
                .include(ThriftBenchmark.class.getSimpleName())//
                .warmupIterations(3)//
                .warmupTime(TimeValue.seconds(10))//
                .measurementIterations(3)//
                .measurementTime(TimeValue.seconds(10))//
                .threads(CONCURRENCY)//
                .forks(1)//
                .build();

        new Runner(opt).run();
    }

}