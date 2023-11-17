package com.bitdf.txing.oj.judge.judge;

import com.bitdf.txing.oj.enume.LanguageEnum;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.judge.strategy.DefaultJudgeStrategy;
import com.bitdf.txing.oj.judge.judge.strategy.JudgeStrategy;
import com.bitdf.txing.oj.utils.SpringContextUtils;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:49:49
 * 注释：判题管理器 根据用户提交的代码语言 选择合适的判题策略来进行判题
 */
public class JudgeManager {
    /**
     * 判题
     * @return
     */
    public static JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeStrategy judgeStrategy = (JudgeStrategy) SpringContextUtils.getBean("defaultJudgeStrategy");
        if (judgeContext.getQuestionSubmit().getLanguage()
                .equals(LanguageEnum.JAVA.getValue())) {
            judgeStrategy = (JudgeStrategy) SpringContextUtils.getBean("javaJudgeStrategy");
        }
        JudgeInfo judgeInfo = judgeStrategy.doJudge(judgeContext);
        return judgeInfo;
    }

}
