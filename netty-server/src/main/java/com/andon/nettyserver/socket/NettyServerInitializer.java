package com.andon.nettyserver.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                .addLast("nettyServerHandler", nettyServerHandler);
    }
}
