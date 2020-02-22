package com.caoyx.rpc.core.net.netty.client;


import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.net.api.Client;
import com.caoyx.rpc.core.net.netty.codec.CaoyxRpcDecoder;
import com.caoyx.rpc.core.net.netty.codec.CaoyxRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @author caoyixiong
 */
@Slf4j
public class NettyClient implements Client {

    private EventLoopGroup group;
    private Channel channel;

    @Override
    public void init(String ipPort) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 10, 4))
                                .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
                                .addLast(new CaoyxRpcEncoder())
                                .addLast(new CaoyxRpcDecoder(CaoyxRpcResponse.class))
                                .addLast(new NettyClientHandler());
                    }
                });
        channel = bootstrap.connect(ipPort.split(":")[0], Integer.valueOf(ipPort.split(":")[1])).sync().channel();
    }


    @Override
    public void doSend(CaoyxRpcRequest requestPacket) {
        if (channel != null) {
            try {
                channel.writeAndFlush(requestPacket).sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
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
}