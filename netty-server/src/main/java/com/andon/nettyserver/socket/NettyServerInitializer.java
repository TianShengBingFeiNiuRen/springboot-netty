package com.andon.nettyserver.socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty服务端初始化配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyServerHandler nettyServerHandler;
    private final AcceptorIdleStateTrigger idleStateTrigger;

    /**
     * 初始化channel
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 分隔符解码器，处理半包
        // maxFrameLength 表示一行最大的长度
        // Delimiters.lineDelimiter()，以/n，/r/n作为分隔符
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        // 自定义心跳检测
        // 1）readerIdleTime：为读超时时间（即多长时间没有接受到客户端发送数据）
        // 2）writerIdleTime：为写超时时间（即多长时间没有向客户端发送数据）
        // 3）allIdleTime：所有类型的超时时间
        pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(idleStateTrigger);
        socketChannel.pipeline().addLast(nettyServerHandler);
    }
}
