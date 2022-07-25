package com.andon.nettyclient.socket;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("建立连接!! channelId:{}", ctx.channel().id());
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("关闭连接!! channelId:{}", ctx.channel().id());
        super.channelInactive(ctx);
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
                ctx.writeAndFlush(heartbeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
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
}
