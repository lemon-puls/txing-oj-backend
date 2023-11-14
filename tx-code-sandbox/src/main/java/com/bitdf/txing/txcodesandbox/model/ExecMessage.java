package com.bitdf.txing.txcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/11/14 12:56:05
 * 注释：用于封装执行结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecMessage {
    /**
     * 程序退出码（0：正常 否则异常）
     */
    private Integer exitCode;
    /**
     * 执行信息
     */
    private String message;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 执行时间
     */
    private Long time;
    /**
     * 占用内存
     */
    private Long memory;

}
