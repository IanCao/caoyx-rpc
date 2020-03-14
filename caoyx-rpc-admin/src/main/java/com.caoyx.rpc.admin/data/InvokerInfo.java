package com.caoyx.rpc.admin.data;

import lombok.Data;

import java.util.Map;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-14 00:46
 */
@Data
public class InvokerInfo {
    private String ip;
    private Map<String, Object> metadata;
}