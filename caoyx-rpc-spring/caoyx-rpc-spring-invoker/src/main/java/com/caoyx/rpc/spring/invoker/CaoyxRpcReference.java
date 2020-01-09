package com.caoyx.rpc.spring.invoker;

import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.netty.client.Client;
import com.caoyx.rpc.core.netty.client.NettyClient;
import com.caoyx.rpc.core.register.RegisterType;
import com.caoyx.rpc.core.serialization.api.SerializerAlgorithm;

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

    Class<? extends Client> client() default NettyClient.class;

    SerializerAlgorithm serializer() default SerializerAlgorithm.JDK;

    String version() default "0";

    RegisterType register() default RegisterType.NO_REGISTER;

    String[] loadAddress() default {};

    CallType callType() default CallType.SYNC;

    String registerAddress() default "";

    String remoteApplicationName() default "";

    int retryTimes() default 0;

    long timeout() default 3000L;

    String[] filters() default {};
}