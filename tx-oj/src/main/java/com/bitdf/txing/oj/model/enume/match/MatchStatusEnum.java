package com.bitdf.txing.oj.model.enume.match;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchStatusEnum {

    NOSTART(0,"未开始"),

    RUNNING(1, "进行中"),

    FINISHED(2,"已结束"),

    JUDGE_FINISHED(3, "作答处理完成");

    private final Integer code;

    private final String msg;
}
