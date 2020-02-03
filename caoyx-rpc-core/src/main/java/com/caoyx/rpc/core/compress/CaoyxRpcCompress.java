package com.caoyx.rpc.core.compress;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-22 11:14
 */
public enum CaoyxRpcCompress {
    INSTANCE;

    private final ConcurrentHashMap<Byte, Compress> compressMap = new ConcurrentHashMap<>();

    public byte[] compress(byte[] data, byte compressType) {
        return getCompress(compressType).compress(data);
    }

    public byte[] decompress(byte[] data, int decompressedLength, byte compressType) {
        return getCompress(compressType).decompress(data, decompressedLength);
    }

    public Compress getCompress(byte compressType) {
        Compress compress = compressMap.get(compressType);
        if (compress != null) {
            return compress;
        }
        compressMap.putIfAbsent(compressType, createCompress(compressType));
        return compressMap.get(compressType);
    }

    public Compress createCompress(byte type) {
        CompressType compressType = CompressType.findByType(type);
        if (compressType == null) {
            throw new CaoyxRpcException(type + "is the not support compressType");
        }
        return (Compress) ExtensionLoader.getExtension(Compress.class, compressType.getLabel()).getValidExtensionInstance();
    }
}