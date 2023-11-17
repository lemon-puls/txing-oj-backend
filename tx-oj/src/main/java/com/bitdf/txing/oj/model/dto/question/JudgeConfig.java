package com.bitdf.txing.oj.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/11/16 11:09:43
 * 注释：题目运行限制
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeConfig {
    /**
     * 时间限制（ms）
     */
    private Long timeLimit;

    /**
     * 内存限制（KB）
     */
    private Long memoryLimit;
}
