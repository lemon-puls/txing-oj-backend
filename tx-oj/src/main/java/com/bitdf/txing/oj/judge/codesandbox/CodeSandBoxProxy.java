package com.bitdf.txing.oj.judge.codesandbox;

import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lizhiwei
 * @date 2023/11/15 23:49:37
 * 注释：代码沙箱代理类 代理模式
 */
@Slf4j
public class CodeSandBoxProxy implements CodeSandBox{
    private CodeSandBox codeSandBox;

    public CodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }

    @Override
    public ExecCodeResponse execCode(ExecCodeRequest request) {
        log.info("调用代码沙箱请求：{}", request.toString());
        ExecCodeResponse response = codeSandBox.execCode(request);
        log.info("调用代码沙箱响应：{}", response.toString());
        return response;
    }
}
