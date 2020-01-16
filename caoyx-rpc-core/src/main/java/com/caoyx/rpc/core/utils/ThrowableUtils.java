package com.caoyx.rpc.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-14 17:13
 */
public class ThrowableUtils {
    public static String throwable2String(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}