package com.caoyx.rpc.spring.invoker;

import com.caoyx.rpc.core.compress.CompressType;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.loadbalance.LoadBalanceType;
import com.caoyx.rpc.core.net.api.Client;
import com.caoyx.rpc.core.net.netty.client.NettyClient;
import com.caoyx.rpc.core.register.RegisterType;
import com.caoyx.rpc.core.serialization.SerializerType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CaoyxRpcReference {

    SerializerType serializer() default SerializerType.PROTOSTUFF;

    String remoteImplVersion() default "0";

    String remoteApplicationName() default "";

    String remoteapplicationVersion() default "0";

    RegisterType register() default RegisterType.NO_REGISTER;

    LoadBalanceType loadBalance() default LoadBalanceType.RANDOM;

    String[] loadAddress() default {};

    CallType callType() default CallType.SYNC;

    String registerAddress() default "";

    int retryTimes() default 0;

    long timeout() default 3000L;

    String[] filters() default {};

    String failCallBack() default "";

    String accessToken() default "";
}