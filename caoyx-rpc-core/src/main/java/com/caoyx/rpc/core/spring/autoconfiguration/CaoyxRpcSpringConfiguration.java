package com.caoyx.rpc.core.spring.autoconfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-26 15:09
 */
@Configuration
@Import({CaoyxRpcProviderSpringConfiguration.class, CaoyxRpcInvokerSpringConfiguration.class})
public class CaoyxRpcSpringConfiguration {
}