package com.bitdf.txing.oj.judge.codesandbox;

import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeResponse;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:34:40
 * 注释：调用第三方代码沙箱
 */
@Component("thirdPartyCodeSandBox")
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecCodeResponse execCode(ExecCodeRequest request) {
        return null;
    }
}
