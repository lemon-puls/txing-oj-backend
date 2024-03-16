package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;

import java.util.List;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-13 21:54:02
 */
public interface QuestionService extends IService<Question> {

    PageUtils queryPage(PageVO queryVO);

    void validQuestion(Question question, boolean b);

    List<QuestionVO> getQuestionVOsByQuestions(List<?> list, boolean b);

    List<QuestionVO> getQuestionVOsByIds(List<Long> questions);
}

