package com.caoyx.rpc.core.netty.client;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcDecoder;
import com.caoyx.rpc.core.netty.codec.CaoyxRpcEncoder;
import com.caoyx.rpc.core.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;
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
    public void init(CaoyxRpcReferenceBean caoyxRpcReferenceBean) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new CaoyxRpcEncoder(CaoyxRpcRequest.class, new JDKSerializerImpl()))
                                .addLast(new CaoyxRpcDecoder(CaoyxRpcResponse.class, new JDKSerializerImpl()))
                                .addLast(new NettyClientHandler(caoyxRpcReferenceBean.getInvokerFactory()));
                    }
                });
        channel = bootstrap.connect(caoyxRpcReferenceBean.getIp(), caoyxRpcReferenceBean.getPort()).sync().channel();
    }

    @Override
    public void stop() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    @Override
    public void doSend(CaoyxRpcRequest requestPacket) throws InterruptedException {
        int a = 0;
        if (channel != null) {
            channel.writeAndFlush(requestPacket).sync();
        }
    }
}