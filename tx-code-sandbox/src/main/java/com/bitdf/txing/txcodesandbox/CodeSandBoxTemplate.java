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
        // 1、保存： 将用户提交的代码保存为文件

        // 2. 编译：将.java文件编译为.class文件

        // 3、执行：执行编译后的字节码文件 拿到执行结果

        // 4、整理结果

        // 5、删除文件：避免造成不必要的空间浪费
        return null;
    }
}
