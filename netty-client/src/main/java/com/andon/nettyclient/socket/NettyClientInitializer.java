package com.andon.nettyclient.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty客户端通道初始化
 */
@Component
@RequiredArgsConstructor
public class NettyClientInitializer extends ChannelInitializer<Channel> {

    private final NettyClientHandler nettyClientHandler;

    /**
     * 初始化channel
     */
    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
                .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                .addLast(nettyClientHandler);
    }
}
