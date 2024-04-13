package com.bitdf.txing.txcodesandbox.controller;

import com.bitdf.txing.txcodesandbox.CodeSandBoxManager;
import com.bitdf.txing.txcodesandbox.JavaDockerCodeSandBox;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeResponse;
import com.bitdf.txing.txcodesandbox.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:15:29
 * 注释：
 */
@RestController
@RequestMapping("/codesandbox")
public class ExecCodeController {
    @Autowired
    JavaDockerCodeSandBox javaDockerCodeSandBox;
    /**
     * 执行代码
     * @return
     */
    @PostMapping("/exec")
    public R execCode(@RequestBody ExecCodeRequest execCodeRequest) {
//        ExecCodeResponse execCodeResponse = javaDockerCodeSandBox.execCode(execCodeRequest);
        ExecCodeResponse execCodeResponse = CodeSandBoxManager.doExec(execCodeRequest);
        return R.ok(execCodeResponse);
    }

//    @PostMapping("/python/exec")
//    public R execPythonCode(@RequestBody ExecCodeRequest request) {
//
//    }


}
