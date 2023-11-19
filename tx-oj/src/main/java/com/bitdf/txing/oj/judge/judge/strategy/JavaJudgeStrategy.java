package com.bitdf.txing.oj.judge.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.bitdf.txing.oj.enume.JudgeMessageEnum;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.judge.JudgeContext;
import com.bitdf.txing.oj.model.dto.question.JudgeCase;
import com.bitdf.txing.oj.model.dto.question.JudgeConfig;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:01:06
 * 注释：Java语言的判题策略
 */
@Component("javaJudgeStrategy")
@Slf4j
public class JavaJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        Question question = judgeContext.getQuestion();
        List<String> inputs = judgeContext.getInputs();
        List<String> outputs = judgeContext.getOutputs();
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        judgeInfo.setTime(time);
        judgeInfo.setMemory(memory);

        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < outputs.size(); i++) {
            if (!outputs.get(i).equals(judgeCaseList.get(i).getOutput())) {
                judgeInfo.setMessage(JudgeMessageEnum.WRONG_ANSWER.getValue());
                // 计算通过用例比例
                Float acceptedRate = (float) i / inputs.size();
                BigDecimal roundedResult = new BigDecimal(acceptedRate).setScale(2, BigDecimal.ROUND_HALF_UP);
                acceptedRate = roundedResult.floatValue();
                judgeInfo.setAcceptedRate(acceptedRate);
                log.info("提交未通过 仅通过 {} %用例", acceptedRate);
                return judgeInfo;
            }
        }
        // 判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (inputs.size() != outputs.size()) {
            judgeInfo.setMessage(JudgeMessageEnum.RUNTIME_ERROR.getValue());
            // 计算通过用例比例
            Float acceptedRate = (float) outputs.size() / inputs.size();
            BigDecimal roundedResult = new BigDecimal(acceptedRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            acceptedRate = roundedResult.floatValue();
            judgeInfo.setAcceptedRate(acceptedRate);
            log.info("提交未通过 仅通过 {} %用例", acceptedRate);
            return judgeInfo;
        }
        // 通过率100%
        judgeInfo.setAcceptedRate(1f);
        // 判断题目限制
        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        if (time > judgeConfig.getTimeLimit()) {
            judgeInfo.setMessage(JudgeMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
            return judgeInfo;
        }
        // Java 程序本身需要额外执行 10 秒钟
        if (memory > judgeConfig.getMemoryLimit()) {
            judgeInfo.setMessage(JudgeMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
            return judgeInfo;
        }
        judgeInfo.setMessage(JudgeMessageEnum.ACCEPTED.getValue());
        return judgeInfo;
    }
}
