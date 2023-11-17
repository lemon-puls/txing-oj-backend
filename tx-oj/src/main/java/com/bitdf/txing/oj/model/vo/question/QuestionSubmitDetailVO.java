package com.bitdf.txing.oj.model.vo.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/11/18 0:18:46
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitDetailVO {

    private Long id;

    private Long times;

    private Long memory;

    private Float exceedPercent;

    private String result;

    private String status;

    private String code;
    private String language;

    private String title;

    private String createTime;
}
