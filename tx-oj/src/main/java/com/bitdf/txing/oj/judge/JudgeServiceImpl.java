package com.bitdf.txing.oj.judge;

import com.bitdf.txing.oj.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lizhiwei
 * @date 2023/11/14 0:44:35
 * 注释：通过doJudge方法调用沙箱执行代码以及判题
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    QuestionSubmitService questionSubmitService;
    @Override
    public void doJudge(Long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
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

        // 获取输入用例

        // 5）根据沙箱的执行结果，设置题目的判题状态和信息

        // 6）修改数据库中的判题结果

    }
}
