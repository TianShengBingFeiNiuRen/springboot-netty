package com.andon.nettyserver.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty服务端初始化配置
 */
@Component
@RequiredArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<Channel> {

    private final NettyServerHandler nettyServerHandler;

    /**
     * 初始化channel
     */
    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
                // 解码器，对接收到的数据进行长度字段解码，也会对数据进行粘包和拆包处理
                .addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2))
                // 编码器，主要是在响应字节数据前面添加字节长度字段
                .addLast(new LengthFieldPrepender(2))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                .addLast("nettyServerHandler", nettyServerHandler);
    }
}
