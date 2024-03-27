package com.bitdf.txing.oj.model.enume.match;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MatchUserStatusEnum {
    NORMAL(0, "正常"),

    GIVEUP(1, "用户放弃"),

    INVAILD(2, "已结束");

    private final Integer code;

    private final String msg;
}
