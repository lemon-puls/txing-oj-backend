package com.bitdf.txing.txcodesandbox;

import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:25:46
 * 注释：Java原生代码沙箱的实现
 */
@Slf4j
@Component
public class JavaNativeCodeSandBox extends CodeSandBoxTemplate {
    @Override
    public ExecCodeResponse execCode(ExecCodeRequest execCodeRequest) {
        return super.execCode(execCodeRequest);
    }
}
