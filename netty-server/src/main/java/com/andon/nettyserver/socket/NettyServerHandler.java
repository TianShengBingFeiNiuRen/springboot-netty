package com.andon.nettyserver.socket;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty服务端处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
// 这个注解适用于标注一个channel handler可以被多个channel安全地共享
// 也可以使用new NettyServerHandler()方式解决
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.info("接收到的消息:{}", msg);
        StringBuilder sb = null;
        Map<String, Object> result = null;
        try {
            // 报文解析处理
            sb = new StringBuilder();
            result = JSONObject.parseObject(msg);

            sb.append(result).append("解析成功").append("\n");
            ctx.writeAndFlush(sb);
        } catch (Exception e) {
            ctx.writeAndFlush("-1\n");
            log.error("报文解析失败:{}", e.getMessage());
        }
    }

    /**
     * 客户端去和服务端连接成功时触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        log.info("收到客户端[ip:" + clientIp + "]连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        ctx.close();
        //把客户端的通道关闭
        ctx.channel().close();
    }
}
