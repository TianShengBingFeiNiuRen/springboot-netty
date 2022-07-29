package com.andon.nettyclient.listener;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.andon.common.constant.NettyPacketType;
import com.andon.common.dto.NettyPacket;
import com.andon.common.event.NettyPacketEvent;
import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Andon
 * 2022/7/27
 * <p>
 * Netty客户端自定义数据包处理监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyClientPacketListener implements ApplicationListener<NettyPacketEvent> {

    @Async
    @Override
    public void onApplicationEvent(NettyPacketEvent event) {
        ChannelId channelId = (ChannelId) event.getSource();
        String nettyPacketType = event.getNettyPacket().getNettyPacketType();
        if (nettyPacketType.equals(NettyPacketType.HEARTBEAT.getValue())) {
            log.info("client receive heartbeat!!");
        } else if (nettyPacketType.equals(NettyPacketType.REQUEST.getValue())) {
            // TODO REQUEST
            log.info("channelId:{} REQUEST!! data:{}", channelId, JSONObject.toJSONString(event.getNettyPacket().getData()));
        } else if (nettyPacketType.equals(NettyPacketType.RESPONSE.getValue())) {
            NettyPacket<Object> nettyResponse = JSONObject.parseObject(JSONObject.toJSONString(event.getNettyPacket()), new TypeReference<NettyPacket<Object>>() {
            }.getType());
            NettyPacket.response(event.getNettyPacket().getRequestId(), nettyResponse);
        } else {
            log.warn("unknown NettyPacketType!! channelId:{} event:{}", channelId, JSONObject.toJSONString(event));
        }
    }
}
