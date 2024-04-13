package com.bitdf.txing.oj.model.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lizhiwei
 * @date 2024/4/12 18:51
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    ENABLE(0, "启用"),
    FORBIDDEN(1, "禁用"),

    SIGNOUT(2, "注销");

    private Integer code;

    private String msg;
}
