package com.andon.nettyclient.socket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty客户端处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Lazy
    @Resource
    private NettyClient nettyClient;

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("建立Netty连接!!");
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("Netty连接关闭!!");
        reconnect(ctx);
    }

    /**
     * 心跳处理，每5秒发送一次心跳请求
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("已经5秒没有发送消息给服务端!!");
                // 向服务端发送心跳包
                String heartbeat = "{\"msg\":\"client heartbeat\"}\n";
                // 发送心跳消息，并在发送失败时关闭该连接
                ctx.writeAndFlush(heartbeat);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 收到服务端消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("收到服务端消息:{}", msg);
    }

    /**
     * 当连接发生异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        ctx.close();
    }

    private void reconnect(ChannelHandlerContext ctx) {
        log.info("准备30s后断线重连!!");
        ctx.channel().eventLoop().schedule(() -> nettyClient.run(), 30, TimeUnit.SECONDS);
    }
}
