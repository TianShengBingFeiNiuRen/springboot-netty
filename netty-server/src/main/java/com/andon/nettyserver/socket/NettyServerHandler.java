package com.andon.nettyserver.socket;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty服务端处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    // 管理一个全局map，保存连接进服务端的通道数量
    public static final Map<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 当客户端主动连接服务端，通道活跃后触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        // 获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();
        // 如果map中不包含此连接，就保存连接
        if (CHANNEL_MAP.containsKey(channelId)) {
            log.info("客户端【{}】是连接状态，连接通道数量:{}", channelId, CHANNEL_MAP.size());
        } else {
            // 保存连接
            CHANNEL_MAP.put(channelId, ctx);
            log.info("客户端【{}】连接Netty服务端!![clientIp:{} clientPort:{}]", channelId, clientIp, clientPort);
            log.info("连接通道数量:{}", CHANNEL_MAP.size());
        }
    }

    /**
     * 当客户端主动断开连接，通道不活跃触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        // 获取终止连接的客户端ID
        ChannelId channelId = ctx.channel().id();
        // 包含此客户端才去删除
        if (CHANNEL_MAP.containsKey(channelId)) {
            // 删除连接
            CHANNEL_MAP.remove(channelId);
            log.warn("客户端【{}】断开Netty连接!![clientIp:{} clientPort:{}]", channelId, clientIp, clientPort);
            log.info("连接通道数量:{}", CHANNEL_MAP.size());
        }
    }

    /**
     * 通道有消息触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("接收到客户端【{}】的消息:{}", ctx.channel().id(), msg.toString());
        StringBuilder sb;
        Map<String, Object> result;
        try {
            // 报文解析处理
            sb = new StringBuilder();
            result = JSONObject.parseObject(msg.toString());
            sb.append(result).append("解析成功!!").append("\n");
            // 响应客户端
            this.channelWrite(ctx.channel().id(), sb);
        } catch (Exception e) {
            ctx.writeAndFlush("-1\n");
            log.error("报文解析失败:{}", e.getMessage());
        }
    }

    public void channelWrite(ChannelId channelId, Object msg) {
        ChannelHandlerContext ctx = CHANNEL_MAP.get(channelId);
        if (ctx == null) {
            log.info("通道【{}】不存在!!", channelId);
            return;
        }
        // 将返回客户端的信息写入ctx
        ctx.write(msg);
        // 刷新缓存区
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.warn("Client: 【{}】 READER_IDLE 读超时", socketString);
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.warn("Client: 【{}】 WRITER_IDLE 写超时", socketString);
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.warn("Client: 【{}】 ALL_IDLE 总超时", socketString);
                ctx.disconnect();
            }
        }
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
