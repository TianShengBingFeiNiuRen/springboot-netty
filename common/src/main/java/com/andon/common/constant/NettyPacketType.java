package com.andon.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Andon
 * 2022/7/27
 */
@Getter
@AllArgsConstructor
public enum NettyPacketType {

    HEARTBEAT("心跳", "HEARTBEAT"),
    REQUEST("请求", "REQUEST"),
    RESPONSE("响应", "RESPONSE");

    private final String name;
    private final String value;
}
