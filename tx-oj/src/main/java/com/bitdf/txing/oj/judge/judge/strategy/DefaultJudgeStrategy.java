package com.bitdf.txing.oj.judge.judge.strategy;

import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.judge.JudgeContext;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:02:05
 * 注释：默认的判题策略
 */
@Component("defaultJudgeStrategy")
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        return null;
    }
}
