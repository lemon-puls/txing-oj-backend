package com.bitdf.txing.oj.model.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lizhiwei
 * @date 2023/12/29 10:04:35
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum UserApplyReadStatusEnum {
    UNREAD(0,"未读"),

    READ(1,"已读");


    private final Integer code;

    private final String msg;
}
