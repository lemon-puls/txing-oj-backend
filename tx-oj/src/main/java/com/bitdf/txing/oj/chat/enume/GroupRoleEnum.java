package com.bitdf.txing.oj.chat.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2024/1/1 13:10:33
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum GroupRoleEnum {

    LEADER(0, "群主"),
    MANAGER(1, "管理员"),
    MEMBER(2, "普通成员"),
    REMOVE(3, "被踢出群聊");

    private final Integer type;

    private final String msg;

    private static Map<Integer, GroupRoleEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleEnum.values()).collect(Collectors.toMap(GroupRoleEnum::getType, Function.identity()));
    }

    public static GroupRoleEnum of(Integer type) {
        return cache.get(type);
    }
}
