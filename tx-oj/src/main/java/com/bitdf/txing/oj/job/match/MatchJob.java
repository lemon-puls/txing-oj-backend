package com.bitdf.txing.oj.job.match;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.enume.match.MatchStatusEnum;
import com.bitdf.txing.oj.service.MatchWeekQuestionRelateService;
import com.bitdf.txing.oj.service.MatchWeekService;
import com.bitdf.txing.oj.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class MatchJob {

    @Autowired
    MatchWeekService matchWeekService;
    @Autowired
    QuestionService questionService;
    @Autowired
    MatchWeekQuestionRelateService matchQuestionRelateService;

    /**
     * 生成比赛 TODO 考虑事务问题
     */
    @Scheduled(cron = "40 28 13 * * *")
    public void createMatch() {
        // 创建周赛
        Date[] dates = getStartEndTime();
        // 获取上一场周赛信息
        WeekMatch lastWeekMatch = matchWeekService.getLastSessionMatch();
        int sessionNo = lastWeekMatch != null ? lastWeekMatch.getSessionNo() + 1 : 1;
        WeekMatch curMatch = WeekMatch.builder()
                .startTime(dates[0])
                .endTime(dates[1])
                .sessionNo(sessionNo)
                .name("第" + sessionNo + "场周赛")
                .status(MatchStatusEnum.NOSTART.getCode())
                .joinCount(0)
                .build();
        matchWeekService.save(curMatch);
        // 抽选题目
        List<Question> questions = questionService.list(new QueryWrapper<Question>().lambda().select(Question::getId));
        List<Question> randomQuestions = selectRandomQuestions(questions, 5);
        matchQuestionRelateService.saveMatchQuestions(curMatch.getId(), randomQuestions);
    }

    /**
     * 抽选题目
     */
    public List<Question> selectRandomQuestions(List<Question> list, int count) {
        List<Question> selectedItems = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count && !list.isEmpty(); i++) {
            int randomIndex = rand.nextInt(list.size());
            selectedItems.add(list.get(randomIndex));
            list.remove(randomIndex); // Ensure no duplicates
        }
        return selectedItems;
    }

    /**
     * 获取比赛开始时间和结束时间
     *
     * @return
     */
    public Date[] getStartEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 30);
        Date endTime = calendar.getTime();
        Date[] res = new Date[]{startTime, endTime};
        return res;
    }
}
