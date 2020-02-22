package com.caoyx.rpc.spring.invoker;

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

    String serializer() default "";

    int providerImplVersion() default 0;

    String providerApplicationName() default "";

    String registerType() default "";

    String loadBalance() default "";

    String callType() default "";

    String address() default "";

    int retry() default 0;

    long timeout() default 3000L;

    String[] filters() default {};

    String failCallBack() default "";

    String accessToken() default "";
}