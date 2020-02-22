package com.caoyx.rpc.benchmark;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:39
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("provider.xml");) {
            context.start();
            Thread.sleep(Integer.MAX_VALUE);
        }
    }
}