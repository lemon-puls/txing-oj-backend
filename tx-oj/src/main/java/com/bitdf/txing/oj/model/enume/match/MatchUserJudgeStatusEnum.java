package com.bitdf.txing.oj.model.enume.match;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MatchUserJudgeStatusEnum {
    WAITTING(0,"未完成"),

    FINISHED(1, "已完成");

    private final Integer code;

    private final String msg;
}
