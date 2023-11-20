package com.bitdf.txing.oj.model.vo.question;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.bitdf.txing.oj.model.dto.question.JudgeCase;
import com.bitdf.txing.oj.model.dto.question.JudgeConfig;
import com.bitdf.txing.oj.model.entity.Question;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/11/20 15:18:44
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionManageVO {
    /**
     * id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 题目内容
     */
    private String content;
    /**
     * 题目标签
     */
    private List<String> tags;
    /**
     * 题目答案
     */
    private String answer;
    /**
     * 提交次数
     */
    private Integer submitNum;
    /**
     * 通过次数
     */
    private Integer acceptedNum;
    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;
    /**
     * 题目配置
     */
    private JudgeConfig judgeConfig;
    /**
     * 点赞数
     */
    private Integer thumbNum;
    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
//    public static Question voToObj(QuestionVO questionVO) {
//        if (questionVO == null) {
//            return null;
//        }
//        Question question = new Question();
//        BeanUtils.copyProperties(questionVO, question);
//        List<String> tagList = questionVO.getTags();
//        if (tagList != null) {
//            question.setTags(JSONUtil.toJsonStr(tagList));
//        }
//        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
//        if (voJudgeConfig != null) {
//            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
//        }
//        return question;
//    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionManageVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionManageVO questionManageVO = new QuestionManageVO();
        BeanUtils.copyProperties(question, questionManageVO);
        // 设置标签
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        questionManageVO.setTags(tagList);
        // 设置判题配置
        String judgeConfigStr = question.getJudgeConfig();
        questionManageVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        // 设置测试用例
        List<JudgeCase> judgeCases = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        questionManageVO.setJudgeCase(judgeCases);
        return questionManageVO;
    }


}
