package com.caoyx.rpc.core.url;

import com.caoyx.rpc.core.enums.URLProtocol;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-20 11:24
 */
@Data
@NoArgsConstructor
public class URL {
    private URLProtocol protocol;
}