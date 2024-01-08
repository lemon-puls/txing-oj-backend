package com.bitdf.txing.oj.chat.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:01:30
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum RoomFriendStatusEnum {
    ACTIVE(0, "正常"),

    FORBIDDEN(1, "禁用");

    private final Integer code;
    private final String msg;
}
