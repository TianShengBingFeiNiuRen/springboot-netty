package com.andon.nettyserver.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty服务端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyServer implements CommandLineRunner {

    private Channel channel;
    // boss事件轮询线程组，处理连接事件
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    // worker事件轮询线程组，用于数据处理
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final NettyServerInitializer nettyServerInitializer;

    @Value("${netty.port}")
    private Integer port;

    /**
     * 开启Netty服务
     */
    @Override
    public void run(String... args) {
        try {
            // 启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 设置参数，组配置
            serverBootstrap.group(bossGroup, workerGroup)
                    // 指定channel
                    .channel(NioServerSocketChannel.class)
                    // 服务端可连接队列数，对应TCP/IP协议listen函数中backlog参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 设置TCP长连接，一般如果两个小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // Netty服务端channel初始化
                    .childHandler(nettyServerInitializer);
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = serverBootstrap.bind(port).sync();
            if (future.isSuccess()) {
                log.info("Netty服务端启动!! 端口:[{}]", port);
            }
            channel = future.channel();
        } catch (Exception e) {
            log.error("Netty服务端启动异常!! error:{}", e.getMessage());
        }
    }

    @PreDestroy
    private void destroy() {
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.warn("Netty服务关闭!!");
    }
}
