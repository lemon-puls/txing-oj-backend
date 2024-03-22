package com.bitdf.txing.oj.service.adapter;


import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;
import com.bitdf.txing.oj.model.enume.match.MatchStatusEnum;
import com.bitdf.txing.oj.model.vo.match.OnlinePkMatchVO;
import com.bitdf.txing.oj.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchPkAdapter {

    @Autowired
    QuestionService questionService;

    public OnlinePkMatch buildOnlinePkMatch(Long userId1, Long userId2) {
        // 用户id排序
        List<Long> userIds = sortUserIdList(Arrays.asList(userId1, userId2));
        // 计算比赛始终时间
        Date startTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 20);
        Date endTime = calendar.getTime();
        // 抽选比赛题目
        List<Question> questions = questionService.getQuestionsByRandom(1);
        ThrowUtils.throwIf(questions.isEmpty(), "题目为空");
        Long questionId = questions.get(0).getId();
        // 构建比赛实体
        OnlinePkMatch onlinePkMatch = OnlinePkMatch.builder()
                .userId1(userIds.get(0))
                .userId2(userIds.get(1))
                .startTime(startTime)
                .endTime(endTime)
                .questionId(questionId)
                .status(MatchStatusEnum.RUNNING.getCode())
                .build();
        return onlinePkMatch;
    }


    /**
     * 对用户id进行排序
     *
     * @param userIds
     * @return
     */
    public static List<Long> sortUserIdList(List<Long> userIds) {
        return userIds.stream().sorted().collect(Collectors.toList());
    }

    public static OnlinePkMatchVO buildOnlinePkMatchVO(OnlinePkMatch match) {
        OnlinePkMatchVO matchVO = new OnlinePkMatchVO();
        BeanUtils.copyProperties(match, matchVO);
        return matchVO;
    }
}
