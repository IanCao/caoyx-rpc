package com.caoyx.rpc.core.net.netty.server;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.net.param.ServerInvokerArgs;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import com.caoyx.rpc.core.shutdown.GraceFullyShutDownCallBack;
import com.caoyx.rpc.core.shutdown.GracefullyShutDown;
import com.caoyx.rpc.core.utils.ThreadPoolUtils;
import com.caoyx.rpc.core.utils.ThrowableUtils;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author caoyixiong
 */
@Slf4j
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<CaoyxRpcRequest> {

    private CaoyxRpcProviderFactory caoyxRpcProviderFactory;
    private ThreadPoolExecutor executor;

    public NettyServerHandler(CaoyxRpcProviderFactory caoyxRpcProviderFactory) {
        this.caoyxRpcProviderFactory = caoyxRpcProviderFactory;
        executor = ThreadPoolUtils.createThreadPool("caoyx-rpc-server-handler");
    }


    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final CaoyxRpcRequest requestPacket) throws Exception {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CaoyxRpcResponse rpcResponsePacket = new CaoyxRpcResponse();
                try {
                    InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
                    ServerInvokerArgs serverInvokerArgs = new ServerInvokerArgs();
                    serverInvokerArgs.setInvokerAddress(new Address(socketAddress.getHostName(), socketAddress.getPort()));
                    serverInvokerArgs.setRequestPacket(requestPacket);
                    rpcResponsePacket = caoyxRpcProviderFactory.invoke(serverInvokerArgs);
                } catch (Exception e) {
                    rpcResponsePacket.setErrorMsg(ThrowableUtils.throwable2String(e));
                    rpcResponsePacket.setStatus(CaoyxRpcStatus.FAIL);
                }
                try {
                    channelHandlerContext.writeAndFlush(rpcResponsePacket).sync();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    public void shutdownGracefully() {
        executor.shutdown();
    }
}