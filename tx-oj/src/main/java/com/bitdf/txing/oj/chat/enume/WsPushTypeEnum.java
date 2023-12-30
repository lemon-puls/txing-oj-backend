package com.bitdf.txing.oj.chat.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:53:45
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum WsPushTypeEnum {
    USER(0, "个人"),

    ALL(1, "全员");

    private final Integer type;

    private final String desc;

    private static Map<Integer, WsPushTypeEnum> cache;

    static {
        cache = Arrays.stream(WsPushTypeEnum.values())
                .collect(Collectors.toMap(WsPushTypeEnum::getType, Function.identity()));
    }

    public static WsPushTypeEnum of(Integer type) {
        return cache.get(type);
    }

}
