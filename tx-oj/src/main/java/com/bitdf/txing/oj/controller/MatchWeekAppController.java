package com.bitdf.txing.oj.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.job.match.MatchJob;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.match.MatchUserStatusEnum;
import com.bitdf.txing.oj.model.vo.match.MatchResultVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchRankItemVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchStartVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchVO;
import com.bitdf.txing.oj.service.MatchUserRelateService;
import com.bitdf.txing.oj.service.MatchWeekService;
import com.bitdf.txing.oj.service.business.MatchAppService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.RedisUtils;
import com.bitdf.txing.oj.utils.page.PageUtils;
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
    @Autowired
    MatchJob matchJob;

    /**
     * 开始周赛
     */
    @GetMapping("/start")
    @AuthCheck(mustRole = "login")
    public R startMatch(@RequestParam(name = "matchId", required = false) Long matchId) {
        WeekMatchStartVO weekMatchStartVO = matchAppService.startMatch(matchId);
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
        Long userRelateId = matchUserRelateService.saveEndTime(request.getMatchId(), user.getId());

        request.setUserId(user.getId());
        rabbitTemplate.convertAndSend(MyMqConfig.JUDGE_EXCHANGE, MyMqConfig.MATCH_HANDLE_ROUTTINGKEY,
                request, new CorrelationData(request.getMatchId() + "" + user.getId()));
        return R.ok(userRelateId);
    }

    @GetMapping("/simulate/result/get")
    @AuthCheck(mustRole = "login")
    public R getSimulateResult(@RequestParam("joinId") Long joinId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        MatchResultVO resultVO = matchAppService.getSimulateResult(joinId, userId);
        return R.ok(resultVO);
    }

    /**
     * 获取周赛结果 个人结果 及 排行榜
     */
    @GetMapping("/rank/get")
//    @AuthCheck(mustRole = "login")
    public R getMatchRank() {
        String str = RedisUtils.get(RedisKeyConstant.MATCH_WEEK_RANK);
//        List<WeekMatchRankVO> collect = strings.stream().map(str -> JSON.parseObject(str, WeekMatchRankVO.class))
//                .collect(Collectors.toList());
        if (ObjectUtil.isNull(str) || "[]".equals(str)) {
            WeekMatchVO lastWeekMatch = matchWeekService.getLastWeekMatch();
            matchAppService.computeMatchRank(lastWeekMatch.getId());
        }
        str = RedisUtils.get(RedisKeyConstant.MATCH_WEEK_RANK);
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
     * 获取上周的周赛信息
     */
    @GetMapping("/last/get")
    public R getLastWeekMatch() {
        WeekMatchVO weekMatchVO = matchWeekService.getLastWeekMatch();
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

    /**
     * 放弃比赛
     */
    @GetMapping("/giveup")
    @AuthCheck(mustRole = "login")
    public R giveUpMatch(@RequestParam("matchId") Long matchId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        matchUserRelateService.updateUserStatus(MatchUserStatusEnum.GIVEUP.getCode(), userId, matchId);
        return R.ok();
    }

    /**
     * 查询当前用户周赛参赛记录
     */
    @PostMapping("/user/record/get")
    @AuthCheck(mustRole = "login")
    public R getWeekMatchRecordByUserId(@RequestBody PageRequest pageRequest) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        PageUtils pageUtils = matchAppService.getWeekMatchRecordByUserId(pageRequest, userId);
        return R.ok(pageUtils);
    }

    /**
     * 查询当前用户模拟记录
     */
    @PostMapping("/user/simulate/record/get")
    @AuthCheck(mustRole = "login")
    public R getWeekSimulateRecordByUserId(@RequestBody PageRequest pageRequest) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        PageUtils pageUtils = matchAppService.getWeekSimulateRecordByUserId(pageRequest, userId);
        return R.ok(pageUtils);
    }


    @GetMapping("/ceshi")
    public R cehsi() {
        rabbitTemplate.convertAndSend(MyMqConfig.DELAYED_EXCHANGE, MyMqConfig.MATCH_WEEK_CHECK_ROUTTINGKEY, 4L,
                correlationData -> {
                    correlationData.getMessageProperties().setDelay(1000 * 10);
                    return correlationData;
                });
        return R.ok();
    }

    @GetMapping("/create")
    public R createWeekMatch() {
        R match = matchJob.createMatch();
        return match;
    }


    @GetMapping("/repeat/join/is")
    @AuthCheck(mustRole = "login")
    public R isRepeatJoin(@RequestParam("matchId") Long matchId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        Boolean b = matchAppService.isRepeatJoin(matchId, userId);
        return R.ok(b);
    }

}
