package com.andon.common.event;

import com.andon.common.dto.NettyPacket;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Andon
 * 2022/7/27
 * <p>
 * 自定义Netty数据包处理事件
 */
@Getter
public class NettyPacketEvent extends ApplicationEvent {

    private NettyPacket<Object> nettyPacket;

    public NettyPacketEvent(Object source, NettyPacket<Object> nettyPacket) {
        super(source);
        this.nettyPacket = nettyPacket;
    }
}
