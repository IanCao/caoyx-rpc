package com.caoyx.rpc.core.netty.client;

import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

/**
 * @author caoyixiong
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<CaoyxRpcResponse> {

    private CaoyxRpcInvokerFactory caoyxRpcInvokerFactory;

    public NettyClientHandler(CaoyxRpcInvokerFactory caoyxRpcInvokerFactory) {
        this.caoyxRpcInvokerFactory = caoyxRpcInvokerFactory;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        caoyxRpcInvokerFactory.setRemoteAddress(remoteAddress);
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CaoyxRpcResponse caoyxRpcResponse) throws Exception {
        System.out.println("NettyClientHandler: " + caoyxRpcResponse.getRequestId());
        caoyxRpcInvokerFactory.notifyResponse(caoyxRpcResponse);
    }
}