package com.bitdf.txing.oj.judge;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:41:42
 * 注释：通过doJudge方法调用沙箱执行代码以及判题
 */
public interface JudgeService {
    void doJudge(Long questionSubmitId);
}
