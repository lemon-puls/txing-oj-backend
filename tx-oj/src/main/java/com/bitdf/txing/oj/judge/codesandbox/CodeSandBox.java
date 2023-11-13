package com.bitdf.txing.oj.judge.codesandbox;

import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeResponse;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:28:16
 * 注释：代码沙箱接口 用于调用代码沙箱执行代码 使用不同的沙箱会有不同的实现
 */
public interface CodeSandBox {
    /**
     * 执行代码方法
     * @param request
     * @return
     */
    ExecCodeResponse execCode(ExecCodeRequest request);
}
