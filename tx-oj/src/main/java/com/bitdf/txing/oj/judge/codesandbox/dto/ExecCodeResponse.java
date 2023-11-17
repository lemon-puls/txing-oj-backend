package com.bitdf.txing.oj.judge.codesandbox.dto;

import com.bitdf.txing.oj.judge.JudgeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:30:32
 * 注释：调用沙箱执行代码得到的响应
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExecCodeResponse {
    /**
     * 输出结果
     */
    List<String> outputs;
    /**
     * 执行信息（错误信息）
     */
    private String message;
    /**
     * 执行状态（1：通过 3：不通过）
     */
    private Integer status;
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
