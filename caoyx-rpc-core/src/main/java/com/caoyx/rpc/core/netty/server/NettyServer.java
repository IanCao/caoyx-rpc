package com.caoyx.rpc.core.netty.server;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcDecoder;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcEncoder;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author caoyixiong
 */
public class NettyServer implements Server {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private EventLoopGroup receiveGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap serverBootstrap;

    public void start(CaoyxRpcProviderFactory caoyxRpcProviderFactory) throws InterruptedException {
        receiveGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(receiveGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_SNDBUF, 32 * 1024) // 发送缓冲区
                .option(ChannelOption.SO_RCVBUF, 32 * 1024) // 接收缓冲区
                .option(ChannelOption.SO_KEEPALIVE, true)   // 保持长连接
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new CaoyxRpcDecoder(CaoyxRpcRequest.class, caoyxRpcProviderFactory.getSerializerInstance()))
                                .addLast(new CaoyxRpcEncoder(CaoyxRpcResponse.class, caoyxRpcProviderFactory.getSerializerInstance()))
                                .addLast(new NettyServerHandler(caoyxRpcProviderFactory));
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(caoyxRpcProviderFactory.getPort()).sync().addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("server bind success");
                } else {
                    System.out.println("server bind failure");
                }
            }
        });
    }

    public void stop() {
        if (receiveGroup != null) {
            receiveGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }
}