package com.caoyx.rpc.core.compress.impl;

import com.caoyx.rpc.core.compress.Compress;
import com.caoyx.rpc.core.extension.annotation.Implement;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-22 13:02
 */
@Implement(name = "noCompress")
public class NoCompress implements Compress {
    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data, int decompressedLength) {
        return data;
    }
}