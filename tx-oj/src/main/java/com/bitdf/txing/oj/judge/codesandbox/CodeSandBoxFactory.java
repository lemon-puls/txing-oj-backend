package com.bitdf.txing.oj.judge.codesandbox;

import com.bitdf.txing.oj.utils.SpringContextUtils;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2023/11/16 8:13:02
 * 注释：代码沙箱工厂
 */
@Component
public class CodeSandBoxFactory {

//    private static CodeSandBox remoteCodeSandBox = new RemoteCodeSandBox();

    /**
     * 根据传入的type返回相对应类型的代码沙箱实例
     *
     * @param type
     * @return
     */
    public static CodeSandBox getInstance(String type) {
        CodeSandBox codeSandBox;
        switch (type) {
            case "remote":
                codeSandBox = (CodeSandBox) SpringContextUtils.getBean("remoteCodeSandBox");
                break;
            case "thirdParty":
                codeSandBox = (CodeSandBox) SpringContextUtils.getBean("thirdPartyCodeSandBox");
                break;
            default:
                codeSandBox = (CodeSandBox) SpringContextUtils.getBean("remoteCodeSandBox");
        }
        return codeSandBox;
    }
}
