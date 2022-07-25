package com.andon.nettyclient.task;

import com.andon.nettyclient.socket.NettyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Andon
 * 2022/7/25
 */
@Component
@RequiredArgsConstructor
public class TaskScheduled implements CommandLineRunner {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private final NettyClient nettyClient;

    /**
     * 模拟业务处理
     */
    @Override
    public void run(String... args) throws Exception {
        // 如果任务里面执行的时间大于 period 的时间，下一次的任务会推迟执行。
        // 本次任务执行完后下次的任务还需要延迟period时间后再执行
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            System.out.println("====定时任务开始====");
            // 发送json字符串
            String msg = "{\"key\":\"hello\",\"value\":\"world\",\"date\":\"" + new Date().toString() + "\"}\n";
            nettyClient.sendMsg(msg);
        }, 2, 10, TimeUnit.SECONDS);
    }
}
