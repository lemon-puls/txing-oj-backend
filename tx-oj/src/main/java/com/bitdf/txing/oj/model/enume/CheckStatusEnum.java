package com.bitdf.txing.oj.model.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lizhiwei
 * @date 2024/4/12 15:51
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum CheckStatusEnum {

    WAITTING(0, "审核中"),
    ACCEPTED(1, "审核通过"),
    REJECT(2, "不通过");

    private Integer code;

    private String msg;
}
