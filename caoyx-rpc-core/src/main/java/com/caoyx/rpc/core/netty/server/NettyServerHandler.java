package com.caoyx.rpc.core.netty.server;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author caoyixiong
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<CaoyxRpcRequest> {

    private CaoyxRpcProviderFactory caoyxRpcProviderFactory;

    public NettyServerHandler(CaoyxRpcProviderFactory caoyxRpcProviderFactory) {
        this.caoyxRpcProviderFactory = caoyxRpcProviderFactory;
    }


    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CaoyxRpcRequest requestPacket) throws Exception {
        System.out.println("NettyServerHandler channelRead0 requestPacket:" + requestPacket.getRequestId());

        CaoyxRpcResponse rpcResponsePacket = caoyxRpcProviderFactory.invoke(requestPacket);
        channelHandlerContext.writeAndFlush(rpcResponsePacket).sync();
    }
}