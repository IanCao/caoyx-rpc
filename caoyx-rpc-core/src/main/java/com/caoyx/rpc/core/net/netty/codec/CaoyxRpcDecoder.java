package com.caoyx.rpc.core.net.netty.codec;

import com.caoyx.rpc.core.compress.CaoyxRpcCompress;
import com.caoyx.rpc.core.data.CaoyxRpcPacket;
import com.caoyx.rpc.core.serialization.CaoyxRpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author caoyixiong
 */
public class CaoyxRpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public CaoyxRpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        int magicNumber = byteBuf.readInt();

        // not caoyxRpc data
        if (magicNumber != CaoyxRpcPacket.MAGIC_NUMBER) {
            return;
        }

        byte serializerAlgorithm = byteBuf.readByte();

        byte compressType = byteBuf.readByte();

        int decompressedLength = byteBuf.readInt();

        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            channelHandlerContext.close();
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        byte[] decompressData = CaoyxRpcCompress.INSTANCE.decompress(data, decompressedLength, compressType);

        Object obj = CaoyxRpcSerializer.INSTANCE.deserialize(genericClass, decompressData, serializerAlgorithm);

        list.add(obj);
    }
}