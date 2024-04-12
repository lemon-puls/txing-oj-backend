package com.bitdf.txing.oj.model.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lizhiwei
 * @date 2024/4/11 19:02
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum CodeExecStatusEnum {

    COMPLIE_ERROR(0, "编译错误"),

    SUCCESS_AC(1, "执行完成并AC"),

    SUCCESS_UNAC(3, "执行完成但不AC");

    private Integer code;

    private String msg;
}
