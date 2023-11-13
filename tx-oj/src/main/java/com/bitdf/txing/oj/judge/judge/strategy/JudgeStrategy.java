package com.bitdf.txing.oj.judge.judge.strategy;

import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.judge.JudgeContext;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:59:38
 * 注释：判题策略接口
 */
public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext judgeContext);
}
