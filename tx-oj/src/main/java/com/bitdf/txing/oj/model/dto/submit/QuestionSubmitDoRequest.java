package com.bitdf.txing.oj.model.dto.submit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/11/15 21:35:47
 * 注释：提交作答
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitDoRequest {
    /**
     * 语言
     */
    private String language;
    /**
     * 代码
     */
    private String code;
    /**
     * 题目Id
     */
    private Long questionId;
}
