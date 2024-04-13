package com.bitdf.txing.txcodesandbox;

import com.bitdf.txing.txcodesandbox.constant.LanguageConstant;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeResponse;
import com.bitdf.txing.txcodesandbox.util.SpringContextUtils;
import com.bitdf.txing.txcodesandbox.util.ThrowUtils;

/**
 * @author lizhiwei
 * @date 2024/4/10 13:30
 * 注释：
 */
public class CodeSandBoxManager {

    public static ExecCodeResponse doExec(ExecCodeRequest execCodeRequest) {
        CodeSandBox codeSandBox = null;
        if (LanguageConstant.JAVA.equals(execCodeRequest.getLanguage())) {
            codeSandBox = (CodeSandBox) SpringContextUtils.getBean("javaDockerCodeSandBox");
        } else if (LanguageConstant.PYTHON.equals(execCodeRequest.getLanguage())) {
            codeSandBox = (CodeSandBox) SpringContextUtils.getBean("pythonDockerCodeSandBox");
        } else if (LanguageConstant.JAVASCRIPT.equals(execCodeRequest.getLanguage())) {
            codeSandBox = (CodeSandBox) SpringContextUtils.getBean("jsDockerCodeSandBox");
        } else if (LanguageConstant.GCC.equals(execCodeRequest.getLanguage())) {
            codeSandBox = (CodeSandBox) SpringContextUtils.getBean("gccDockerCodeSandBox");
        }
        ThrowUtils.throwIf(codeSandBox == null, "编程语言非法");
        return codeSandBox.execCode(execCodeRequest);
    }
}
