package com.caoyx.rpc.core.compress.impl;

import com.caoyx.rpc.core.compress.Compress;
import com.caoyx.rpc.core.extension.annotation.Implement;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-22 11:14
 */
@Implement(name = "lz4")
public class Lz4Compress implements Compress {

    private LZ4Factory factory;

    public Lz4Compress() {
        factory = LZ4Factory.fastestInstance();
    }

    @Override
    public byte[] compress(byte[] data) {
        LZ4Compressor compressor = factory.fastCompressor();
        return compressor.compress(data);
    }

    @Override
    public byte[] decompress(byte[] data, int decompressedLength) {
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        return decompressor.decompress(data, decompressedLength);
    }
}