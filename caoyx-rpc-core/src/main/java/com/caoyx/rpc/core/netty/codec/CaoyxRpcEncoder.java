package com.caoyx.rpc.core.netty.codec;

import com.caoyx.rpc.core.data.CaoyxRpcPacket;
import com.caoyx.rpc.core.data.Packet;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.serializer.CaoyxRpcSerializer;
import com.caoyx.rpc.core.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author caoyixiong
 */
public class CaoyxRpcEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        if (!(object instanceof CaoyxRpcPacket)) {
            throw new CaoyxRpcException("CaoyxRpcEncoder class is not caoyxRpc Packet");
        }
        CaoyxRpcPacket caoyxRpcPacket = (CaoyxRpcPacket) object;
        //magic num
        byteBuf.writeInt(CaoyxRpcPacket.MAGIC_NUMBER);
        //serializer Algorithm
        byte serializerAlgorithm = caoyxRpcPacket.getSerializerAlgorithm();
        byteBuf.writeByte(serializerAlgorithm);

        byte[] data = CaoyxRpcSerializer.INSTANCE.serialize(object, serializerAlgorithm);
        // data length
        byteBuf.writeInt(data.length);
        // data
        byteBuf.writeBytes(data);
    }
}