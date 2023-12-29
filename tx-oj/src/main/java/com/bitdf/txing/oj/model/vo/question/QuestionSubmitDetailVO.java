package com.bitdf.txing.oj.model.vo.question;

import cn.hutool.json.JSONUtil;
import com.bitdf.txing.oj.model.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;

/**
 * @author Lizhiwei
 * @date 2023/11/18 0:18:46
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitDetailVO {

    private Long id;

    private Long times;

    private Long memory;

    private Float exceedPercent;

    private String result;

    private String status;

    private String code;
    private String language;

    private String title;

    private String createTime;

    /**
     * 转换
     *
     * @param questionSubmit
     * @param question
     * @return
     */
    public static QuestionSubmitDetailVO toQuestionSubmitDetailVO(QuestionSubmit questionSubmit, Question question) {
        QuestionSubmitDetailVO questionSubmitDetailVO = new QuestionSubmitDetailVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitDetailVO);
        //设置状态
        questionSubmitDetailVO.setStatus(JudgeStatusEnum.getByValue(questionSubmit.getStatus()).getText());
        JudgeInfo judgeInfo = JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class);
        questionSubmitDetailVO.setMemory(judgeInfo.getMemory());
        questionSubmitDetailVO.setTimes(judgeInfo.getTime());
        questionSubmitDetailVO.setResult(judgeInfo.getMessage());
        if (questionSubmit.getCreateTime() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            String format = simpleDateFormat.format(questionSubmit.getCreateTime());
            questionSubmitDetailVO.setCreateTime(format);
        } else {
            questionSubmitDetailVO.setCreateTime("");
        }
        // 查询题目
        questionSubmitDetailVO.setTitle(question.getTitle());
        questionSubmitDetailVO.setCode(questionSubmit.getCode());
        questionSubmitDetailVO.setLanguage(questionSubmit.getLanguage());
        return questionSubmitDetailVO;
    }

}
