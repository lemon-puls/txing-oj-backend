package com.bitdf.txing.oj.judge.codesandbox;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeResponse;
import com.bitdf.txing.oj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:33:13
 * 注释：调用远程代码沙箱
 */
@Component("remoteCodeSandBox")
@Slf4j
public class RemoteCodeSandBox implements CodeSandBox {
    @Override
    public ExecCodeResponse execCode(ExecCodeRequest request) {
        String url = "http://124.71.1.148:8082/codesandbox/exec";
        String requestStr = JSONUtil.toJsonStr(request);
        String responseStr = HttpUtil.createPost(url)
                .body(requestStr)
                .execute()
                .body();
        R r = JSONUtil.toBean(responseStr, R.class);
        JSONObject data = (JSONObject) r.get("data");
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION, "远程调用代码沙箱出错");
        }
        ExecCodeResponse execCodeResponse = JSONUtil.toBean(data, ExecCodeResponse.class);
        log.info("完成代码沙箱的调用，执行结果：{}", execCodeResponse);
        return execCodeResponse;
    }
}
