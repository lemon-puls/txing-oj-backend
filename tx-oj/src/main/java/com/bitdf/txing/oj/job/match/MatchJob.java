package com.bitdf.txing.oj.job.match;

import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.enume.match.MatchStatusEnum;
import com.bitdf.txing.oj.service.MatchWeekQuestionRelateService;
import com.bitdf.txing.oj.service.MatchWeekService;
import com.bitdf.txing.oj.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MatchJob {

    @Autowired
    MatchWeekService matchWeekService;
    @Autowired
    QuestionService questionService;
    @Autowired
    MatchWeekQuestionRelateService matchQuestionRelateService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 生成比赛 TODO 考虑事务问题
     */
    @Scheduled(cron = "00 00 13 * * 6")
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
        List<Question> randomQuestions = questionService.getQuestionsByRandom(5);
        matchQuestionRelateService.saveMatchQuestions(curMatch.getId(), randomQuestions);
        // 发送消息到 延时交换机 用于比赛结束后检查比赛状态以及统计比赛结果
        long delayTimes = curMatch.getEndTime().getTime() - System.currentTimeMillis() + (1000 * 10);
        rabbitTemplate.convertAndSend(MyMqConfig.DELAYED_EXCHANGE, MyMqConfig.MATCH_WEEK_CHECK_ROUTTINGKEY, curMatch.getId(),
                correlationData -> {
                    correlationData.getMessageProperties().setDelay((int) delayTimes);
                    return correlationData;
                });
    }

    /**
     * 获取比赛开始时间和结束时间
     *
     * @return
     */
    public Date[] getStartEndTime() {
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        int diffDays = 7 - i + 7;
        calendar.add(Calendar.DAY_OF_MONTH, diffDays);
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
