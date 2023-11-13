package com.bitdf.txing.txcodesandbox;

import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeResponse;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:22:30
 * 注释：使用了 模板方法设计模式 将执行代码过程拆分为多个步骤（方法），这样一来，不同的代码沙箱只需要根据自己的实际情况 调整某个步骤的实现即可 而无需写大量的重复代码
 */
public abstract class CodeSandBoxTemplate implements CodeSandBox {
    @Override
    public ExecCodeResponse execCode(ExecCodeRequest execCodeRequest) {
        return null;
    }
}
