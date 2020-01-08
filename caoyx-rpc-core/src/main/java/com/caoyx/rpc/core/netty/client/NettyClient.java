package com.caoyx.rpc.core.netty.client;

import com.caoyx.rpc.core.data.CaoyxRpcPacket;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcDecoder;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcEncoder;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.serializer.CaoyxRpcSerializer;
import com.caoyx.rpc.core.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author caoyixiong
 */
public class NettyClient implements Client {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private EventLoopGroup group;
    private Channel channel;

    @Override
    public void init(Address address, final CaoyxRpcInvokerFactory invokerFactory) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new CaoyxRpcEncoder())
                                .addLast(new CaoyxRpcDecoder(CaoyxRpcResponse.class))
                                .addLast(new NettyClientHandler(invokerFactory));
                    }
                });
        channel = bootstrap.connect(address.getIp(), address.getPort()).sync().channel();
    }


    @Override
    public void doSend(CaoyxRpcRequest requestPacket) {
        if (channel != null) {
            try {
                channel.writeAndFlush(requestPacket).sync();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    @Override
    public boolean isValid() {
        return channel != null && channel.isActive();
    }

    private ByteBuf request2ByteBuf(CaoyxRpcRequest rpcRequest) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        //magic num
        byteBuf.writeInt(CaoyxRpcPacket.MAGIC_NUMBER);
        //serializer Algorithm
        byte serializerAlgorithm = rpcRequest.getSerializerAlgorithm();
        byteBuf.writeByte(serializerAlgorithm);

        byte[] data = new byte[0];
        try {
            data = CaoyxRpcSerializer.INSTANCE.serialize(rpcRequest, serializerAlgorithm);
        } catch (CaoyxRpcException e) {
            e.printStackTrace();
        }
        // data length
        byteBuf.writeInt(data.length);
        // data
        byteBuf.writeBytes(data);
        return byteBuf;
    }
}