package com.caoyx.rpc.core.extension.annotation;

import com.caoyx.rpc.core.enums.ExtensionType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-03 12:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    ExtensionType type() default ExtensionType.UNKNOW;
}