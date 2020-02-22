package com.caoyx.rpc.benchmark.caoyxRpc;

import com.caoyx.rpc.benchmark.base.Page;
import com.caoyx.rpc.benchmark.base.User;
import com.caoyx.rpc.benchmark.base.UserService;
import com.caoyx.rpc.benchmark.base.UserServiceImpl;
import com.caoyx.rpc.core.compress.CompressType;
import com.caoyx.rpc.core.config.CaoyxRpcInvokerConfig;
import com.caoyx.rpc.core.invoker.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.register.RegisterType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:33
 */
@State(Scope.Benchmark)
public class CaoyxRpcBenchMark {
    private UserService userService;
    private UserService userServiceImpl = new UserServiceImpl();
    private final AtomicInteger counter = new AtomicInteger(0);

    public CaoyxRpcBenchMark() {
        CaoyxRpcInvokerConfig config = new CaoyxRpcInvokerConfig();
        config.setIFace(UserService.class);
        config.setApplicationName("caoyxRpc-benchmark-client");
        config.setProviderApplicationName("caoyxRpc-benchmark-server");
        config.setRegisterConfig(new RegisterConfig("127.0.0.1:1118", RegisterType.DIRECT));

        CaoyxRpcReferenceBean rpcReferenceBean = new CaoyxRpcReferenceBean(config);
        userService = (UserService) rpcReferenceBean.getObject();
    }


    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean existUser() throws Exception {
        return userService.existUser(counter.getAndIncrement() + "");
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean createUser() throws Exception {
        User user = userServiceImpl.getUser(counter.incrementAndGet());
        return userService.createUser(user);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public User getUser() throws Exception {
        return userService.getUser(counter.getAndIncrement());
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Page<User> listUser() throws Exception {
        return userService.listUser(counter.getAndIncrement());
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()//
                .include(CaoyxRpcBenchMark.class.getSimpleName())//
                .warmupIterations(3)//
                .warmupTime(TimeValue.seconds(10))//
                .measurementIterations(3)//
                .measurementTime(TimeValue.seconds(10))//
                .threads(32)//
                .forks(1)//
                .build();

        new Runner(opt).run();
    }
}