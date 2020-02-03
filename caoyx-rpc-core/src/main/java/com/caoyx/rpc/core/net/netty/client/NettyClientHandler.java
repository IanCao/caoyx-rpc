package com.caoyx.rpc.core.net.netty.client;

import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcPendingInvokerPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @author caoyixiong
 */
@Slf4j
@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<CaoyxRpcResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CaoyxRpcResponse caoyxRpcResponse) throws Exception {
//        log.info("NettyClientHandler channelRead0 :" + caoyxRpcResponse.getRequestId());
        CaoyxRpcPendingInvokerPool.INSTANCE.notifyResponse(caoyxRpcResponse);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            channel.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}