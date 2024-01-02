package com.bitdf.txing.oj.model.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lizhiwei
 * @date 2024/1/1 13:38:20
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum UserActiveStatusEnum {

    OFFLINE(0,"下线"),

    ONLINE(1,"上线");

    private final Integer code;

    private final String msg;
}
