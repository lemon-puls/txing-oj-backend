package com.bitdf.txing.oj.service.business.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.JudgeService;
import com.bitdf.txing.oj.mapper.MatchSubmitRelateMapper;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.entity.match.MatchSubmitRelate;
import com.bitdf.txing.oj.model.entity.match.MatchUserRelate;
import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.entity.match.WeekMatchQuestionRelate;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.model.enume.match.*;
import com.bitdf.txing.oj.model.vo.match.WeekMatchRankItemVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchStartVO;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.bitdf.txing.oj.service.*;
import com.bitdf.txing.oj.service.adapter.MatchSubmitAdapter;
import com.bitdf.txing.oj.service.adapter.MatchUserAdapter;
import com.bitdf.txing.oj.service.business.MatchAppService;
import com.bitdf.txing.oj.service.cache.UserCache;
import com.bitdf.txing.oj.utils.RedisUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MatchAppServiceImpl implements MatchAppService {

    @Autowired
    MatchWeekService matchWeekService;
    @Autowired
    QuestionService questionService;
    @Autowired
    MatchWeekQuestionRelateService matchWeekQuestionRelateService;
    @Autowired
    MatchSubmitRelateService matchSubmitRelateService;
    @Autowired
    QuestionSubmitService questionSubmitService;
    @Autowired
    MatchUserRelateService matchUserRelateService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    JudgeService judgeService;
    @Autowired
    MatchSubmitRelateMapper matchSubmitRelateMapper;
    @Autowired
    UserCache userCache;

    /**
     * 开始周赛
     *
     * @return
     */
    @Override
    public WeekMatchStartVO startMatch(Long matchId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        WeekMatch lastSessionMatch;
        MatchJoinTypeEnum joinTypeEnum = Objects.isNull(matchId) ? MatchJoinTypeEnum.FORMAT : MatchJoinTypeEnum.SIMULATE;
        if (Objects.isNull(matchId)) {
            // 正式比赛
            lastSessionMatch = matchWeekService.getLastSessionMatch();
            boolean isStarted = new Date().compareTo(lastSessionMatch.getStartTime()) >= 0
                    && new Date().compareTo(lastSessionMatch.getEndTime()) < 0;
            ThrowUtils.throwIf(lastSessionMatch == null || !isStarted, "比赛无效");
        } else {
            // 模拟
            // 判断是否有未完成的模拟赛
            MatchUserRelate matchRunning = matchUserRelateService.getSimulateMatchRunning(userId);
//            ThrowUtils.throwIf(matchRunning != null && !matchRunning.getMatchId().equals(matchId), "请先完成");
            if (matchRunning != null && !matchRunning.getMatchId().equals(matchId)) {
                matchId = matchRunning.getMatchId();
            }
            lastSessionMatch = matchWeekService.getById(matchId);
            // 校验 是否已结束
            boolean isFinished = new Date().compareTo(lastSessionMatch.getEndTime()) > 0;
            ThrowUtils.throwIf(!isFinished, "该比赛未完成");
        }
        // 保存 用户参加记录
        MatchUserRelate matchUserRelate = matchUserRelateService.getOne(new QueryWrapper<MatchUserRelate>().lambda()
                .eq(MatchUserRelate::getMatchId, lastSessionMatch.getId())
                .eq(MatchUserRelate::getUserId, userId)
                .eq(MatchUserRelate::getJudgeStatus, MatchUserJudgeStatusEnum.WAITTING.getCode())
                .eq(MatchUserRelate::getStatus, MatchUserStatusEnum.NORMAL.getCode()));
        if (matchUserRelate == null) {
            matchUserRelate = MatchUserAdapter.buildMatchUserRelate(lastSessionMatch.getId(),
                    userId, joinTypeEnum.getCode());
            matchUserRelateService.save(matchUserRelate);
        }

        List<WeekMatchQuestionRelate> list = matchWeekQuestionRelateService.list(new QueryWrapper<WeekMatchQuestionRelate>()
                .lambda()
                .eq(WeekMatchQuestionRelate::getMatchId, lastSessionMatch.getId())
                .orderByAsc(WeekMatchQuestionRelate::getQuestionOrder)
                .select(WeekMatchQuestionRelate::getQuestionId));
        List<Long> questionIds = list.stream().map(item -> item.getQuestionId()).collect(Collectors.toList());
        List<QuestionVO> questionVOs = questionService.getQuestionVOsByIds(questionIds);
        WeekMatchStartVO weekMatchStartVO = new WeekMatchStartVO();
        BeanUtils.copyProperties(lastSessionMatch, weekMatchStartVO);
        weekMatchStartVO.setQuestions(questionVOs);
        if (matchId != null) {
            //模拟
            weekMatchStartVO.setSimulateStartTime(matchUserRelate.getStartTime());
        }
        return weekMatchStartVO;
    }

    /**
     * 提交单条题目
     *
     * @param matchSubmitSingleRequest
     */
    @Override
    public Long submitSingle(MatchSubmitSingleRequest matchSubmitSingleRequest) {
        User user = AuthInterceptor.userThreadLocal.get();
        // 查出参加id
        MatchUserRelate matchUserRelate = matchUserRelateService.getLastJoinRecord(matchSubmitSingleRequest.getMatchId(), user.getId());
        Long submitId = saveOrUpdateSubmit(matchSubmitSingleRequest, matchSubmitSingleRequest.getMatchId(), user.getId(), matchUserRelate.getId());
        if (submitId != -1) {
            rabbitTemplate.convertAndSend(MyMqConfig.JUDGE_EXCHANGE, MyMqConfig.WAITTING_JUDGE_ROUTINGKEY,
                    submitId, new CorrelationData(submitId.toString()));
        }
        return submitId;
    }

    /**
     * 保存或者更新提交
     *
     * @param request
     * @param matchId
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateSubmit(QuestionSubmitDoRequest request, Long matchId, Long userId, Long joinRecordId) {
        MatchSubmitRelate submitRelate = matchSubmitRelateService.getOne(new QueryWrapper<MatchSubmitRelate>().lambda()
                .eq(MatchSubmitRelate::getMatchId, matchId)
                .eq(MatchSubmitRelate::getQuestionId, request.getQuestionId())
                .eq(MatchSubmitRelate::getJoinRecordId, joinRecordId));
        QuestionSubmit questionSubmit = MatchSubmitAdapter.buildQuestionSubmit(request, userId);
        Long submitId = -1L;
        if (Objects.isNull(submitRelate)) {
            questionSubmitService.save(questionSubmit);
            // 查出参与id
            MatchUserRelate matchUserRelate = matchUserRelateService.getById(joinRecordId);
            // 还未提交过
            MatchSubmitRelate matchSubmitRelate = MatchSubmitRelate.builder()
                    .matchId(matchId)
                    .matchType(MatchTypeEnum.WEEK.getCode())
                    .questionId(request.getQuestionId())
                    .submitId(questionSubmit.getId())
                    .userId(userId)
                    .joinRecordId(matchUserRelate.getId())
                    .build();
            matchSubmitRelateService.save(matchSubmitRelate);
            submitId = questionSubmit.getId();
        } else {
            // 已经提交过了
            // 查出原来的 看看代码是否有改动
            QuestionSubmit oldSubmit = questionSubmitService.getById(submitRelate.getSubmitId());
            if (!oldSubmit.getCode().equals(request.getCode()) || !oldSubmit.getLanguage().equals(request.getLanguage())
                    || oldSubmit.getStatus().equals(JudgeStatusEnum.WAITTING.getValue())) {
                questionSubmit.setId(submitRelate.getSubmitId());
                questionSubmitService.updateById(questionSubmit);
                submitId = submitRelate.getSubmitId();
            }
        }
        return submitId;
    }

    /**
     * 提交作答
     *
     * @param request
     */
    @Override
    @Transactional
    public void submitAll(MatchSubmitBatchRequest request) {
        // 查出参加id
        MatchUserRelate matchUserRelate = matchUserRelateService.getLastJoinRecord(request.getMatchId(), request.getUserId());
        // 保存作答 收集需要执行判题的submitId
        List<Long> needJudgeSubmitIds = new ArrayList<>();
        for (QuestionSubmitDoRequest submitDoRequest : request.getSubmits()) {
            Long submitId = saveOrUpdateSubmit(submitDoRequest, request.getMatchId(), request.getUserId(), matchUserRelate.getId());
            if (submitId != -1) {
                needJudgeSubmitIds.add(submitId);
            }
        }
        // 执行判题
        for (Long needJudgeSubmitId : needJudgeSubmitIds) {
            judgeService.doJudge(needJudgeSubmitId);
        }
        // 查出所有submit
        List<QuestionSubmit> submitList = matchSubmitRelateMapper.getSubmitsOfUser(request.getMatchId(), matchUserRelate.getId());
        // 获取当前用户ac题目数
        // 获取当前用户未AC题目通过用例和
        int acCount = 0;
        int unAcRateCount = 0;
        for (QuestionSubmit questionSubmit : submitList) {
            if (ObjectUtil.isNotNull(questionSubmit.getExceedPercent()) && questionSubmit.getExceedPercent() != -1) {
                // 本题ac
                acCount++;
            } else {
                JudgeInfo judgeInfo = JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class);
                unAcRateCount += judgeInfo.getAcceptedRate();
            }
        }
        // 更新当前用户的判题状态、ac数目等数据
        boolean update = matchUserRelateService.update(new UpdateWrapper<MatchUserRelate>().lambda()
                .eq(MatchUserRelate::getMatchId, request.getMatchId())
                .eq(MatchUserRelate::getUserId, request.getUserId())
                .set(MatchUserRelate::getJudgeStatus, MatchUserJudgeStatusEnum.FINISHED.getCode())
                .set(MatchUserRelate::getAcCount, acCount)
                .set(MatchUserRelate::getUnAcRateSum, unAcRateCount));
        if (MatchJoinTypeEnum.FORMAT.getCode().equals(matchUserRelate.getJoinType())) {
            // 判断是否本次周赛是否所有用户作答都已处理完毕 并且 比赛已结束 如果是 则计算参赛用户排名
            WeekMatch weekMatch = matchWeekService.getById(request.getMatchId());
            if (weekMatch.getEndTime().compareTo(new Date()) <= 0) {
                int count = matchUserRelateService.count(new QueryWrapper<MatchUserRelate>().lambda()
                        .eq(MatchUserRelate::getMatchId, request.getMatchId())
                        .eq(MatchUserRelate::getJudgeStatus, MatchUserJudgeStatusEnum.WAITTING.getCode())
                        .eq(MatchUserRelate::getStatus, MatchUserStatusEnum.NORMAL.getCode()));
                if (count == 0) {
                    // 开始计算用户排名
                    computeMatchRank(request.getMatchId());
                    // 更改本周比赛状态
                    boolean update1 = matchWeekService.update(new UpdateWrapper<WeekMatch>().lambda()
                            .eq(WeekMatch::getId, request.getMatchId())
                            .set(WeekMatch::getStatus, MatchStatusEnum.JUDGE_FINISHED.getCode()));
                }
            }
        }
        // TODO 如果执行失败进行重试
    }

    /**
     * 计算比赛排名
     *
     * @param matchId
     */
    @Override
    public void computeMatchRank(Long matchId) {
        QueryWrapper<MatchUserRelate> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(MatchUserRelate::getMatchId, matchId)
                .eq(MatchUserRelate::getJoinType, MatchJoinTypeEnum.FORMAT.getCode())
                .eq(MatchUserRelate::getStatus, MatchUserStatusEnum.NORMAL.getCode())
                .orderByDesc(MatchUserRelate::getAcCount, MatchUserRelate::getUnAcRateSum);
        List<MatchUserRelate> list = matchUserRelateService.list(wrapper);
        List<WeekMatchRankItemVO> rankCacheList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MatchUserRelate matchUserRelate = list.get(i);
            matchUserRelate.setGradeRank(i + 1);
            User user = userCache.get(matchUserRelate.getUserId());
            WeekMatchRankItemVO rankVO = WeekMatchRankItemVO.builder()
                    .userId(user.getId())
                    .rank(i + 1)
                    .avatar(user.getUserAvatar())
                    .userName(user.getUserName())
                    .build();
            rankCacheList.add(rankVO);
        }
        // 将排名更新到数据库
        boolean batchById = matchUserRelateService.updateBatchById(list);
        // 将排名信息保存到redis 有效期为一周
        Boolean b = RedisUtils.set(RedisKeyConstant.MATCH_WEEK_RANK, rankCacheList, 60 * 60 * 24 * 7);
    }
}
