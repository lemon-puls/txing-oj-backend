package com.bitdf.txing.oj.controller;

import com.alibaba.fastjson.JSON;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.match.WeekMatchRankItemVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchStartVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchVO;
import com.bitdf.txing.oj.service.MatchUserRelateService;
import com.bitdf.txing.oj.service.MatchWeekService;
import com.bitdf.txing.oj.service.business.MatchAppService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.RedisUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("match/week")
public class MatchWeekAppController {
    @Autowired
    MatchAppService matchAppService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    MatchUserRelateService matchUserRelateService;
    @Autowired
    MatchWeekService matchWeekService;

    /**
     * 开始周赛
     */
    @GetMapping("/start")
    @AuthCheck(mustRole = "login")
    public R startMatch() {
        WeekMatchStartVO weekMatchStartVO = matchAppService.startMatch();
        return R.ok(weekMatchStartVO);
    }

    /**
     * 单条题目作答提交
     */
    @PostMapping("/submit/single")
    @AuthCheck(mustRole = "login")
    public R submitSingle(@RequestBody MatchSubmitSingleRequest matchSubmitSingleRequest) {
        Long submitId = matchAppService.submitSingle(matchSubmitSingleRequest);
        return R.ok(submitId);
    }

    /**
     * 结束 提交作答
     */
    @PostMapping("/submit/all")
    @AuthCheck(mustRole = "login")
    public R submitAll(@RequestBody MatchSubmitBatchRequest request) {
        User user = AuthInterceptor.userThreadLocal.get();
//        matchAppService.submitAll(request, user.getId());
//        String jsonStr = JSON.toJSONString(request);
        // 保存提交时间
        matchUserRelateService.saveEndTime(request.getMatchId(), user.getId());

        request.setUserId(user.getId());
        rabbitTemplate.convertAndSend(MyMqConfig.JUDGE_EXCHANGE, MyMqConfig.MATCH_HANDLE_ROUTTINGKEY,
                request, new CorrelationData(request.getMatchId() + "" + user.getId()));
        return R.ok();
    }

    /**
     * 获取周赛结果 个人结果 及 排行榜
     */
    @GetMapping("/rank/get")
    @AuthCheck(mustRole = "login")
    public R getMatchRank() {
        String str = RedisUtils.get(RedisKeyConstant.MATCH_WEEK_RANK);
//        List<WeekMatchRankVO> collect = strings.stream().map(str -> JSON.parseObject(str, WeekMatchRankVO.class))
//                .collect(Collectors.toList());
        List<WeekMatchRankItemVO> weekMatchRankItemVOS = JSON.parseArray(str, WeekMatchRankItemVO.class);
        return R.ok(weekMatchRankItemVOS);
    }

    /**
     * 获取到最新的周赛信息
     */
    @GetMapping("/next/get")
    public R getNextMatch() {
        WeekMatchVO weekMatchVO = matchWeekService.getNextMatch();
        return R.ok(weekMatchVO);
    }

    /**
     * 获取历史周赛数据
     */
    @GetMapping("/history/get")
    public R getWeekMatchHistory() {
        List<WeekMatchVO> list = matchWeekService.getHistoryMatch();
        return R.ok(list);
    }

}
