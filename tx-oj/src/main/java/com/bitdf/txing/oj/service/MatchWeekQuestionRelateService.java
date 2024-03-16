package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.match.WeekMatchQuestionRelate;

import java.util.List;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2024-03-13 15:00:49
 */
public interface MatchWeekQuestionRelateService extends IService<WeekMatchQuestionRelate> {
    void saveMatchQuestions(Long id, List<Question> questions);

//    PageUtils queryPage(Map<String, Object> params);
}

