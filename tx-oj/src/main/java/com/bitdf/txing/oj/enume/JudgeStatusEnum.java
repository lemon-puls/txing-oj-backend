package com.bitdf.txing.oj.enume;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author Lizhiwei
 * @date 2023/11/15 23:09:01
 * 注释：判题状态
 */
@AllArgsConstructor
public enum JudgeStatusEnum {

    WAITTING("等待中", 0),
    JUDGEING("判题中", 1),
    SUCCESS("成功", 2),
    FAILURE("失败", 3),
    EMIT_MQ_FAILURE("系统异常", 4);

    String text;
    Integer value;

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeStatusEnum getByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeStatusEnum anEnum : JudgeStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }
    }
