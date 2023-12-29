package com.bitdf.txing.oj.model.enume;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lizhiwei
 * @date 2023/12/29 10:01:37
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum UserApplyStatusEnum {
    WAITTING(0, "等待通过"),

    AGREE(1, "同意申请");


    private final Integer code;

    private final String msg;

}
