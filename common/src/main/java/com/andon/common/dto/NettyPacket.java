package com.andon.common.dto;

import com.andon.common.constant.NettyPacketType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Andon
 * 2022/7/27
 * <p>
 * 自定义Netty数据包
 */
@Data
@Slf4j
public class NettyPacket<T> implements Serializable {

    public static Map<String, NettyPacket<Object>> RESPONSE = new ConcurrentHashMap<>();

    private String requestId;
    private String nettyPacketType;
    private String command;

    private int code;
    private T data;
    private String message;
    private int total;

    public static <T> NettyPacket<T> buildRequest(T param) {
        NettyPacket<T> nettyPacket = new NettyPacket<>();
        nettyPacket.setRequestId(getOnlyId());
        nettyPacket.setNettyPacketType(NettyPacketType.REQUEST.getValue());
        nettyPacket.setData(param);
        nettyPacket.setCommand(command(param));
        return nettyPacket;
    }

    public static <T> NettyPacket<T> buildRequest(String requestId, T param) {
        NettyPacket<T> nettyPacket = new NettyPacket<>();
        nettyPacket.setRequestId(getOnlyId());
        nettyPacket.setNettyPacketType(requestId);
        nettyPacket.setData(param);
        nettyPacket.setCommand(command(param));
        return nettyPacket;
    }

    public static <T> NettyPacket<T> buildResponse(T data) {
        NettyPacket<T> nettyPacket = new NettyPacket<>();
        nettyPacket.setRequestId(getOnlyId());
        nettyPacket.setNettyPacketType(NettyPacketType.RESPONSE.getValue());
        nettyPacket.setData(data);
        nettyPacket.setCommand(command(data));
        return nettyPacket;
    }

    public static <T> NettyPacket<T> buildResponse(String requestId, T data) {
        NettyPacket<T> nettyPacket = new NettyPacket<>();
        nettyPacket.setRequestId(requestId);
        nettyPacket.setNettyPacketType(NettyPacketType.RESPONSE.getValue());
        nettyPacket.setData(data);
        nettyPacket.setCommand(command(data));
        return nettyPacket;
    }

    public static void response(String requestId, NettyPacket<Object> nettyResponse) {
        RESPONSE.put(requestId, nettyResponse);
    }

    public NettyPacket<Object> futureResponse() throws Exception {
        long start = System.currentTimeMillis();
        NettyPacket<Object> nettyResponse = RESPONSE.get(requestId);
        while (nettyResponse == null) {
            long cost = System.currentTimeMillis() - start;
            if (cost > TimeUnit.MINUTES.toMillis(1)) {
                throw new RuntimeException("requestId:" + requestId + " 超时，耗时" + cost + "ms");
            }
            TimeUnit.MILLISECONDS.sleep(10);
            nettyResponse = RESPONSE.get(requestId);
        }
        return RESPONSE.remove(requestId);
    }

    public static String getOnlyId() {
        return UUID.randomUUID().toString();
    }

    public static final String REQUEST_TEST_1 = "1";
    public static final String RESPONSE_TEST_1 = "2";

    public static <T> String command(T t) {
        if (t instanceof RequestTestVO) {
            return REQUEST_TEST_1;
        } else if (t instanceof ResponseTestVO) {
            return RESPONSE_TEST_1;
        }
        return null;
    }
}
