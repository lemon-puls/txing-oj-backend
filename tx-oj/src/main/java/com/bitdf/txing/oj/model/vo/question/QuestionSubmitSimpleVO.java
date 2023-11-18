package com.bitdf.txing.oj.model.vo.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/11/18 0:46:27
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitSimpleVO {
    private Long id;

    private Long times;

    private Long memory;

    private String result;

    private Float exceedPercent;

    private String status;

    private String title;
    private String createTime;
}
