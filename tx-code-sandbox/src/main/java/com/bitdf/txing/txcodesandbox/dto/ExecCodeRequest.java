package com.bitdf.txing.txcodesandbox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:31:28
 * 注释：调用沙箱时的请求参数
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExecCodeRequest {
    /**
     * 代码语言
     */
    private String language;
    /**
     * 代码
     */
    private String code;
    /**
     * 输入用例
     */
    private List<String> inputs;
}
