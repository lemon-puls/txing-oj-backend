package com.bitdf.txing.oj.model.vo.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author lizhiwei
 * @date 2024/4/14 10:12
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChartDataVO {
    /**
     * 过去10天 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private List<Date> dates;
    /**
     * 过去10天 做题数
     */
    private List<Integer> questionCounts;
    /**
     * 过去10天 提交数
     */
    private List<Integer> submitCounts;
    /**
     * 过去10天 通过数
     */
    private List<Integer> acCounts;
    /**
     * 过去10天 通过率
     */
    private List<Integer> acRates;
}
