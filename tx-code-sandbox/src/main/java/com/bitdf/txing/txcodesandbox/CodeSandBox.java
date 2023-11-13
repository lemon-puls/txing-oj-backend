package com.bitdf.txing.txcodesandbox;

import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeResponse;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:20:56
 * 注释：代码沙箱接口
 */
public interface CodeSandBox {
    /**
     * 执行代码
     * @param execCodeRequest
     * @return
     */
    ExecCodeResponse execCode(ExecCodeRequest execCodeRequest);
}
