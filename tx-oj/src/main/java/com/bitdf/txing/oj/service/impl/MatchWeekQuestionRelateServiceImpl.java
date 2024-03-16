package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.MatchWeekQuestionRelateMapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.match.WeekMatchQuestionRelate;
import com.bitdf.txing.oj.service.MatchWeekQuestionRelateService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("matchWeekQuestionRelateService")
public class MatchWeekQuestionRelateServiceImpl extends ServiceImpl<MatchWeekQuestionRelateMapper, WeekMatchQuestionRelate> implements MatchWeekQuestionRelateService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MatchWeekQuestionRelateEntity> page = this.page(
//                new Query<MatchWeekQuestionRelateEntity>().getPage(params),
//                new QueryWrapper<MatchWeekQuestionRelateEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    /**
     * 保存比赛题目信息
     * @param matchId
     * @param questions
     */
    @Override
    public void saveMatchQuestions(Long matchId, List<Question> questions) {
        List<WeekMatchQuestionRelate> list = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            WeekMatchQuestionRelate questionRelate = WeekMatchQuestionRelate.builder()
                    .matchId(matchId)
                    .questionId(questions.get(i).getId())
                    .questionOrder(i + 1)
                    .build();
            list.add(questionRelate);
        }
        this.saveBatch(list);
    }
}