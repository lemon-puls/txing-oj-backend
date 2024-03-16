package com.bitdf.txing.oj.model.enume.match;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MatchTypeEnum {
    ONLINEPK(0,"在线PK"),

    WEEK(1, "周赛");

    private final Integer code;

    private final String msg;
}
