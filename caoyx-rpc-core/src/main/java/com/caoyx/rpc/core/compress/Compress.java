package com.caoyx.rpc.core.compress;

import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.extension.annotation.SPI;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-22 11:13
 */
@SPI(type = ExtensionType.COMPRESS)
public interface Compress {
    byte[] compress(byte[] data);

    byte[] decompress(byte[] data, int decompressedLength);
}