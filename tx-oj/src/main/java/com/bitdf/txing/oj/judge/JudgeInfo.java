package com.bitdf.txing.oj.judge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:53:13
 * 注释：存储判题信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JudgeInfo {
    /**
     * 执行用时
     */
    private Long time;
    /**
     * 空间使用
     */
    private Long memory;
    /**
     * 判题信息
     */
    private String message;
    /**
     * 通过用例比例
     */
    private Float acceptedRate;
    /**
     * 超过用户比例
     */
    private Float exceedPercent;

}
