package com.bitdf.txing.txcodesandbox.controller;

import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeResponse;
import com.bitdf.txing.txcodesandbox.util.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:15:29
 * 注释：
 */
@RestController("/codesandbox")
public class ExecCodeController {
    /**
     * 执行代码
     * @return
     */
    @PostMapping("/exec")
    public R execCode(@RequestBody ExecCodeRequest execCodeRequest) {

        ExecCodeResponse execCodeResponse = new ExecCodeResponse();
        return R.ok(execCodeResponse);
    }
}
