package com.caoyx.rpc.core.netty.client;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcDecoder;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcEncoder;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
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
    public void init(Address address, CaoyxRpcInvokerFactory invokerFactory) throws InterruptedException {
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
    public void doSend(CaoyxRpcRequest requestPacket) throws InterruptedException {
        if (channel != null) {
            channel.writeAndFlush(requestPacket).sync();
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
}