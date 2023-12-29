package com.bitdf.txing.oj.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bitdf.txing.oj.model.enume.JudgeMessageEnum;
import com.bitdf.txing.oj.model.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.judge.codesandbox.CodeSandBox;
import com.bitdf.txing.oj.judge.codesandbox.CodeSandBoxFactory;
import com.bitdf.txing.oj.judge.codesandbox.CodeSandBoxProxy;
import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.oj.judge.codesandbox.dto.ExecCodeResponse;
import com.bitdf.txing.oj.judge.judge.JudgeContext;
import com.bitdf.txing.oj.judge.judge.JudgeManager;
import com.bitdf.txing.oj.model.dto.question.JudgeCase;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.service.QuestionService;
import com.bitdf.txing.oj.service.QuestionSubmitService;
import com.bitdf.txing.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:44:35
 * 注释：通过doJudge方法调用沙箱执行代码以及判题
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Value("${codesandbox.type:remote}")
    private String type;

    @Autowired
    @Lazy
    QuestionSubmitService questionSubmitService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public void doJudge(Long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        Question question = questionService.getById(questionSubmit.getQuestionId());
        if (question == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus()
                .equals(JudgeStatusEnum.WAITTING.getValue())) {
            log.info("状态不为等待中 无需重复判题");
            return;
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        questionSubmit.setStatus(JudgeStatusEnum.JUDGEING.getValue());
        boolean b = questionSubmitService.updateById(questionSubmit);
        if (!b) {
            throw new BusinessException(TxCodeEnume.JUDGE_SUMBIT_STATUS_MODIFY_EXCEPTION);
        }
        // 4）调用沙箱，获取到执行结果
        // 获取代码沙箱实例
        CodeSandBox codeSandBox = CodeSandBoxFactory.getInstance(type);
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codeSandBox);
        // 获取输入用例
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        List<String> inputs = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 构建执行代码请求
        ExecCodeRequest execCodeRequest = ExecCodeRequest.builder()
                .code(code)
                .language(language)
                .inputs(inputs).build();
        ExecCodeResponse execCodeResponse = codeSandBoxProxy.execCode(execCodeRequest);
        log.info("完成代码沙箱的调用，执行结果：{}", execCodeResponse);
        // 5）根据沙箱的执行结果，进行判题
        JudgeContext judgeContext = JudgeContext.builder()
                .judgeInfo(execCodeResponse.getJudgeInfo())
                .judgeCaseList(judgeCaseList)
                .outputs(execCodeResponse.getOutputs())
                .inputs(inputs)
                .questionSubmit(questionSubmit)
                .question(question).build();
        JudgeInfo judgeInfo = JudgeManager.doJudge(judgeContext);
        // 6) 计算超过百分之多少的人
        if (JudgeMessageEnum.ACCEPTED.getValue().equals(judgeInfo.getMessage())) {
            QueryWrapper<QuestionSubmit> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(QuestionSubmit::getQuestionId, question.getId())
                    .eq(QuestionSubmit::getStatus, 2);
            List<QuestionSubmit> questionSubmitList = questionSubmitService.list(wrapper);
            List<QuestionSubmit> collect = questionSubmitList.stream().filter((item) -> {
                JudgeInfo judgeInfo1 = JSONUtil.toBean(item.getJudgeInfo(), JudgeInfo.class);
                return judgeInfo1.getAcceptedRate() != null
                        && judgeInfo1.getAcceptedRate().intValue() == 1
                        && judgeInfo1.getMessage().equals(JudgeMessageEnum.ACCEPTED.getValue());
            }).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                List<Long> collect1 = collect.stream().map((item) -> {
                    JudgeInfo judgeInfo1 = JSONUtil.toBean(item.getJudgeInfo(), JudgeInfo.class);
                    return judgeInfo1.getTime() + judgeInfo1.getMemory();
                }).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                // 当次提交分数
                Long curScore = judgeInfo.getTime() + judgeInfo.getMemory();
                int rank = Collections.binarySearch(collect1, curScore, Comparator.reverseOrder());
                if (rank < 0) {
                    rank = -rank - 1;
                }
                Float exceedPercent = (float) rank / collect1.size();
                BigDecimal roundedResult = new BigDecimal(exceedPercent).setScale(2, BigDecimal.ROUND_HALF_UP);
                exceedPercent = roundedResult.floatValue();
                questionSubmit.setExceedPercent(exceedPercent);
                judgeInfo.setExceedPercent(exceedPercent);
            } else {
                questionSubmit.setExceedPercent(1f);
                judgeInfo.setExceedPercent(1f);
            }
        }
        // 7）修改数据库中的判题结果
        String jsonStr = JSONUtil.toJsonStr(judgeInfo);
        questionSubmit.setJudgeInfo(jsonStr);
        questionSubmit.setStatus(JudgeStatusEnum.SUCCESS.getValue());
        log.info("{}", questionSubmit);
        boolean b1 = questionSubmitService.updateById(questionSubmit);
        if (!b1) {
            throw new BusinessException(TxCodeEnume.JUDGE_SUMBIT_INFO_MODIFY_EXCEPTION);
        }
        // 8) 修改题目的提交次数
        // TODO 要把submitNum、AcceptedNum等字段的默认值改为0 以免为null进行计算出错
        boolean isAccepted = JudgeMessageEnum.ACCEPTED.getValue().equals(judgeInfo.getMessage());
        question.setSubmitNum(question.getSubmitNum() + 1);
        if (isAccepted) {
            question.setAcceptedNum(question.getAcceptedNum() + 1);
        }
        // 9) 修改用户个人提交次数等信息
        // 计算该用户刷题数
//        User loginUser = AuthInterceptor.userThreadLocal.get();

//        long count1 = questionSubmitService.count(new QueryWrapper<QuestionSubmit>().lambda()
//                .eq(QuestionSubmit::getUserId, loginUser.getId()));
        QueryWrapper<QuestionSubmit> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(QuestionSubmit::getUserId, questionSubmit.getUserId())
                .eq(QuestionSubmit::getQuestionId, question.getId())
                .ge(QuestionSubmit::getExceedPercent, 0);
//                .groupBy(QuestionSubmit::getQuestionId).having("question = max(qustionId)").select(QuestionSubmit::getQuestionId);
        long count = questionSubmitService.count(wrapper);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(User::getId, questionSubmit.getUserId())
                .setSql("submit_count = submit_count + 1")
                .setSql(isAccepted, "accepted_count = accepted_count + 1")
                .setSql(count == 1 && isAccepted, "question_count = question_count + 1");
        boolean update = userService.update(updateWrapper);
        ThrowUtils.throwIf(!update, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);

        boolean b2 = questionService.updateById(question);
        ThrowUtils.throwIf(!b2, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
    }
}
