package com.bitdf.txing.oj.model.enume.match;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MatchJoinTypeEnum {
    FORMAT(0,"正式"),

    SIMULATE(1, "模拟");

    private final Integer code;

    private final String msg;
}
