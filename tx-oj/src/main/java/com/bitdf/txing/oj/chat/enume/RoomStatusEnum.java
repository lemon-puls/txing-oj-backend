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
public enum RoomStatusEnum {
    ACTIVE(0, "正常"),

    DISSOLVE(1, "解散"),
    FORBIDDEN(2, "封禁");


    private final Integer code;
    private final String msg;
}
