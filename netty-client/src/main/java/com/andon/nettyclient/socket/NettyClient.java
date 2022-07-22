package com.andon.nettyclient.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Andon
 * 2022/7/22
 * <p>
 * Netty客户端
 */
@Slf4j
@Component
public class NettyClient implements CommandLineRunner {

    @Value("${netty.host}")
    private String host;
    @Value("${netty.port}")
    private Integer port;

    private SocketChannel socketChannel;
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    public void sendMsg(String msg) {
        socketChannel.writeAndFlush(msg);
    }

    @Override
    public void run(String... args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    // 设置TCP的长连接，默认的 keepalive的心跳时间是两个小时
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 将小的数据包包装成更大的帧进行传送，提高网络的负载，即TCP延迟传输
                    .option(ChannelOption.TCP_NODELAY, true)
                    // Netty客户端channel初始化
                    .handler(new NettyClientInitializer());
            // 连接服务器ip、端口
            ChannelFuture future = bootstrap.connect(host, port);

            //客户端断线重连逻辑
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    log.info("连接Netty服务端成功!!");
                } else {
                    log.warn("连接Netty服务端失败，进行断线重连!!");
                    future1.channel().eventLoop().schedule((Runnable) this::run, 20, TimeUnit.SECONDS);
                }
            });
            socketChannel = (SocketChannel) future.channel();
            // 模拟业务处理
            handler();
        } catch (Exception e) {
            log.error("连接Netty服务端异常!! error:{}", e.getMessage());
        } finally {
            group.shutdownGracefully();
            log.warn("Netty服务关闭!!");
        }
    }

    /**
     * 模拟业务处理
     */
    private void handler() {
        // 如果任务里面执行的时间大于 period 的时间，下一次的任务会推迟执行。
        // 本次任务执行完后下次的任务还需要延迟period时间后再执行
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            System.out.println("====定时任务开始====");
            // 发送json字符串
            String msg = "{\"key\":\"hello\",\"value\":\"world\"}\n";
            sendMsg(msg);
        }, 2, 10, TimeUnit.SECONDS);
    }
}
