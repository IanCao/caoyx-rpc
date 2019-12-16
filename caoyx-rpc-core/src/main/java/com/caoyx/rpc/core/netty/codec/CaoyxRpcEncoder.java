package com.caoyx.rpc.core.netty.codec;

import com.caoyx.rpc.core.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author caoyixiong
 */
public class CaoyxRpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;
    private Serializer serializer;

    public CaoyxRpcEncoder(Class<?> genericClass, Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        System.out.println("CaoyxRpcDecoder encode");
        if (genericClass.isInstance(object)) {
            byte[] data = serializer.serialize(object);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
            System.out.println("CaoyxRpcDecoder genericClass");
        }
    }
}