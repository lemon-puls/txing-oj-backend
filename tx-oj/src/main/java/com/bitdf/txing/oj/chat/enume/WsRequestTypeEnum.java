package com.bitdf.txing.oj.chat.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2024/1/8 0:12:06
 * 注释：
 */
@AllArgsConstructor
@Getter
public enum WsRequestTypeEnum {

    HEARTBEAT(0, "心跳报文");

    private final Integer type;
    private final String desc;

    private static Map<Integer, WsRequestTypeEnum> cache;

    static {
        cache = Arrays.stream(WsRequestTypeEnum.values()).collect(Collectors.toMap(WsRequestTypeEnum::getType, Function.identity()));
    }
    public static WsRequestTypeEnum of(Integer type){
        return cache.get(type);
    }
}
