package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.chat.service.adapter.WsAdapter;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.JudgeService;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.dto.question.JudgeConfig;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.model.enume.match.MatchStatusEnum;
import com.bitdf.txing.oj.model.vo.match.*;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import com.bitdf.txing.oj.service.*;
import com.bitdf.txing.oj.service.adapter.MatchPkAdapter;
import com.bitdf.txing.oj.service.adapter.MatchSubmitAdapter;
import com.bitdf.txing.oj.service.adapter.UserAdapter;
import com.bitdf.txing.oj.service.cache.UserCache;
import com.bitdf.txing.oj.utils.MqProducer;
import com.bitdf.txing.oj.utils.RedisUtils;
import com.bitdf.txing.oj.utils.page.PageUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MatchOnlinepkAppServiceImpl implements MatchOnlinepkAppService {

    @Autowired
    MatchOnlinepkService matchOnlinepkService;
    @Autowired
    MatchPkAdapter matchPkAdapter;
    @Autowired
    PushService pushService;
    @Autowired
    QuestionSubmitService questionSubmitService;
    @Autowired
    MqProducer mqProducer;
    @Autowired
    JudgeService judgeService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserCache userCache;
    @Autowired
    UserService userService;

    /**
     * 匹配对手
     *
     * @return
     */
    @Override
    public Long findOpponent() {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.MATCH_PK_FIND_SET);
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        boolean b = RedisUtils.sIsMember(key, userId);
        if (b) {
            return -1L;
        }
        OnlinePkMatch onlinePkMatch;
        String targetId;
        synchronized (this) {
            targetId = RedisUtils.sPop(key);
            if (targetId == null || targetId.equals(userId.toString())) {
                // 暂时没有对手 需要等待
                RedisUtils.sSet(key, userId);
                return -1L;
            }
            // 获取成功 保存到数据库
            onlinePkMatch = matchPkAdapter.buildOnlinePkMatch(userId, Long.valueOf(targetId));
            boolean save = matchOnlinepkService.save(onlinePkMatch);
        }
        // Ws通知对手
        pushService.sendPushMsg(
                WsAdapter.buildPKTeamUpNotifyVO(new WsOnlinePkTeamUpVO(onlinePkMatch.getId())),
                Arrays.asList(Long.valueOf(targetId)), new Date().getTime());
        // 发送消息到 延时交换机 用于比赛结束后检查比赛状态以及统计比赛结果
        long delayTimes = onlinePkMatch.getEndTime().getTime() - System.currentTimeMillis();
        mqProducer.sendMsgWithDelay(MyMqConfig.DELAYED_EXCHANGE, MyMqConfig.MATCH_PK_CHECK_ROUTTINGKEY,
                onlinePkMatch.getId(), delayTimes + (1000 * 10));
        return onlinePkMatch.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(MatchSubmitSingleRequest request) {

        User user = AuthInterceptor.userThreadLocal.get();
        Long submitId = saveOrUpdateSubmit(request, request.getMatchId(), user.getId());
        if (submitId != -1) {
//            mqProducer.sendMsg(MyMqConfig.JUDGE_EXCHANGE, MyMqConfig.WAITTING_JUDGE_ROUTINGKEY,
//                    submitId, submitId.toString());
            judgeService.doJudge(submitId);
        }
        // 判断是否已结束
        if (request.isFinished()) {
            // 更新结束时间
            matchOnlinepkService.finishMatch(request.getMatchId(), user.getId());
            checkAndBuildPkMatchResult(request.getMatchId());
        }
        return submitId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndBuildPkMatchResult(Long matchId) {
        synchronized (String.valueOf(matchId).intern()) {
            OnlinePkMatch onlinePkMatch = matchOnlinepkService.isFinished(matchId);
            if (onlinePkMatch != null) {
                // TODO 可以检查一下双方的提交是否都已处理完成（是否有提交）
                // 已结束
                // 更新比赛状态
                matchOnlinepkService.updateMatchStatus(matchId, MatchStatusEnum.FINISHED.getCode());
                // 计算比赛结果（通过率 60% 运行时间 + 内存 20% 答题用时 20%）
                OnlinePKResultVO onlinePKResultVO = computeMatchResult(matchId,
                        onlinePkMatch.getSubmitId1(), onlinePkMatch.getSubmitId2());
                // 更新获胜者
                OnlinePkMatch update = new OnlinePkMatch();
                update.setId(onlinePkMatch.getId());
                update.setWinnerId(onlinePKResultVO.getWinnerId());
                update.setScore1(onlinePKResultVO.getScore1());
                update.setScore2(onlinePKResultVO.getScore2());
                matchOnlinepkService.updateById(update);
                // 更新获胜者竞赛总积分（+20分）
                if (onlinePKResultVO.getWinnerId() != 0) {
                    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda()
                            .eq(User::getId, onlinePKResultVO.getWinnerId())
                            .setSql("match_score = match_score + 20");
                    boolean update1 = userService.update(updateWrapper);
                }
                return true;
            } else {
                return true;
            }
        }
    }

    /**
     * 计算比赛结果
     *
     * @param matchId
     * @param submitId1
     * @param submitId2
     * @return
     */
    private OnlinePKResultVO computeMatchResult(Long matchId, Long submitId1, Long submitId2) {
        OnlinePkMatch onlinePkMatch = matchOnlinepkService.getById(matchId);
        Double score1;
        Double score2;
        Question question = questionService.getById(onlinePkMatch.getQuestionId());
        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        Long totalSecond = (onlinePkMatch.getEndTime().getTime() - onlinePkMatch.getStartTime().getTime()) / 1000;
        JudgeInfo judgeInfo1 = null;
        JudgeInfo judgeInfo2 = null;
        Long useSeconds1 = null;
        Long useSeconds2 = null;
        if (submitId1 == null) {
            score1 = 0d;
        } else {
            QuestionSubmit questionSubmit1 = questionSubmitService.getById(submitId1);
            judgeInfo1 = JSONUtil.toBean(questionSubmit1.getJudgeInfo(), JudgeInfo.class);
            // 答题用时
            useSeconds1 = ((onlinePkMatch.getSubmitTime1() == null
                    ? onlinePkMatch.getEndTime().getTime() : onlinePkMatch.getSubmitTime1().getTime())
                    - onlinePkMatch.getStartTime().getTime()) / 1000;
            score1 = computeSubmitScore(judgeInfo1, questionSubmit1, judgeConfig, useSeconds1, totalSecond);
        }
        if (submitId2 == null) {
            score2 = 0d;
        } else {
            QuestionSubmit questionSubmit2 = questionSubmitService.getById(submitId2);
            judgeInfo2 = JSONUtil.toBean(questionSubmit2.getJudgeInfo(), JudgeInfo.class);
            useSeconds2 = ((onlinePkMatch.getSubmitTime2() == null
                    ? onlinePkMatch.getEndTime().getTime() : onlinePkMatch.getSubmitTime2().getTime())
                    - onlinePkMatch.getStartTime().getTime()) / 1000;
            score2 = computeSubmitScore(judgeInfo2, questionSubmit2, judgeConfig, useSeconds2, totalSecond);
        }
        OnlinePKResultVO resultVO = new OnlinePKResultVO();
        resultVO.setUserId1(onlinePkMatch.getUserId1());
        resultVO.setUserId2(onlinePkMatch.getUserId2());
        resultVO.setScore1(score1);
        resultVO.setScore2(score2);
        Long winnerId = 0L;
        if (score1 > score2) {
            winnerId = resultVO.getUserId1();
        } else if (score1 < score2) {
            winnerId = resultVO.getUserId2();
        } else if (score1 == 0) {
            winnerId = 0L;
        } else if (score1 < 60) {
            long l = (judgeInfo1.getTime() + judgeInfo1.getMemory()) - (judgeInfo2.getTime() + judgeInfo2.getMemory());
            if (l < 0) {
                winnerId = resultVO.getUserId1();
            } else if (l > 0) {
                winnerId = resultVO.getUserId2();
            } else {
                if (useSeconds1 < useSeconds2) {
                    winnerId = resultVO.getUserId1();
                } else if (useSeconds1 > useSeconds2) {
                    winnerId = resultVO.getUserId2();
                }
            }
        }
        resultVO.setWinnerId(winnerId);
        return resultVO;
    }

    /**
     * 计算作答分数（通过率 60% 运行时间 10%  内存 10% 答题用时 20%）
     *
     * @param judgeInfo
     * @return
     */
    @Override
    public Double computeSubmitScore(JudgeInfo judgeInfo, QuestionSubmit questionSubmit,
                                     JudgeConfig judgeConfig, Long useSeconds, Long totalSeconds) {
        double score = 0;
        if (questionSubmit.getExceedPercent() == -1 || questionSubmit.getExceedPercent() == null) {
            // 未AC
            score += 60 * judgeInfo.getAcceptedRate();
        } else {
            // AC
            // 基础得分
            score += 60;
            // 时间得分
            String timePercent = new DecimalFormat("0.00")
                    .format((double) (judgeConfig.getTimeLimit() - judgeInfo.getTime()) / judgeConfig.getTimeLimit());
            score += Double.valueOf(timePercent) * 10;
            // 内存得分
            String memoryPercent = new DecimalFormat("0.00")
                    .format((double) (judgeConfig.getMemoryLimit() - judgeInfo.getMemory()) / judgeConfig.getMemoryLimit());
            score += Double.valueOf(memoryPercent) * 10;
            // 作答时间得分
            if (totalSeconds != null && useSeconds != null) {
                String useSecondPercent = new DecimalFormat("0.00")
                        .format((double) (totalSeconds - useSeconds) / totalSeconds);
                Double percent = Double.valueOf(useSecondPercent) < 0 ? 0 : Double.valueOf(useSecondPercent);
                score += percent * 20;
            }
        }
        return (double) Math.round(score);
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
    public Long saveOrUpdateSubmit(QuestionSubmitDoRequest request, Long matchId, Long userId) {
        OnlinePkMatch onlinePkMatch = matchOnlinepkService.getById(matchId);

        QuestionSubmit questionSubmit = MatchSubmitAdapter.buildQuestionSubmit(request, userId);
        Long submitId = onlinePkMatch.getUserId1().equals(userId) ?
                onlinePkMatch.getSubmitId1() : onlinePkMatch.getSubmitId2();
        if (Objects.isNull(submitId) || submitId == -1) {
            // 还未提交过
            questionSubmitService.save(questionSubmit);
            submitId = questionSubmit.getId();
            // 保存提交id
            if (onlinePkMatch.getUserId1().equals(userId)) {
                onlinePkMatch.setSubmitId1(submitId);
            } else {
                onlinePkMatch.setSubmitId2(submitId);
            }
            matchOnlinepkService.updateById(onlinePkMatch);
        } else {
            // 已经提交过了
            // 查出原来的 看看代码是否有改动
            QuestionSubmit oldSubmit = questionSubmitService.getById(submitId);
            if (!oldSubmit.getCode().equals(request.getCode()) || !oldSubmit.getLanguage().equals(request.getLanguage())
                    || oldSubmit.getStatus().equals(JudgeStatusEnum.WAITTING.getValue())) {
                questionSubmit.setId(submitId);
                questionSubmitService.updateById(questionSubmit);
            } else {
                submitId = -1L;
            }
        }
        return submitId;
    }

    /**
     * 获取PK结果
     *
     * @param matchId
     * @return
     */
    @Override
    public OnlinePKResultVO getPkResult(Long matchId) {
        OnlinePkMatch onlinePkMatch = matchOnlinepkService.getById(matchId);
        if (onlinePkMatch.getStatus().equals(MatchStatusEnum.RUNNING.getCode())) {
            // 比赛尚未结束
            return null;
        }
        return buildOnlinePKResultVO(onlinePkMatch);
    }

    @Override
    public OnlinePKResultVO buildOnlinePKResultVO(OnlinePkMatch onlinePkMatch) {
        OnlinePKResultVO resultVO = new OnlinePKResultVO();
        BeanUtils.copyProperties(onlinePkMatch, resultVO);
        UserShowVO userVO1 = new UserShowVO();
        UserShowVO userVO2 = new UserShowVO();
        User user1 = userCache.get(onlinePkMatch.getUserId1());
        User user2 = userCache.get(onlinePkMatch.getUserId2());
        BeanUtils.copyProperties(user1, userVO1);
        BeanUtils.copyProperties(user2, userVO2);
        resultVO.setUserVo1(userVO1);
        resultVO.setUserVo2(userVO2);
        // 计算用时
        Long useSeconds1 = ((onlinePkMatch.getSubmitTime1() == null
                ? onlinePkMatch.getEndTime().getTime() : onlinePkMatch.getSubmitTime1().getTime())
                - onlinePkMatch.getStartTime().getTime()) / 1000;
        Long useSeconds2 = ((onlinePkMatch.getSubmitTime2() == null
                ? onlinePkMatch.getEndTime().getTime() : onlinePkMatch.getSubmitTime2().getTime()) - onlinePkMatch.getStartTime().getTime()) / 1000;
        resultVO.setUseSeconds1(useSeconds1);
        resultVO.setUseSeconds2(useSeconds2);
        return resultVO;
    }

    /**
     * 获取当前用户pK记录（结果）
     *
     * @param userId
     * @return
     */
    @Override
    public List<OnlinePKResultVO> getPkRecords(Long userId) {
        List<OnlinePkMatch> list = matchOnlinepkService.getMatchsByUserId(userId);
        List<OnlinePKResultVO> collect = list.stream().map(match -> {
            OnlinePKResultVO pkResult = getPkResult(match.getId());
            return pkResult;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public PkMatchStartVO startPk(Long matchId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        // 获取比赛
        OnlinePkMatch match = matchOnlinepkService.getById(matchId);
        OnlinePkMatchVO matchVO = MatchPkAdapter.buildOnlinePkMatchVO(match);
        // 获取题目
        List<QuestionVO> questionVOS = questionService.getQuestionVOsByIds(Arrays.asList(match.getQuestionId()));
        ThrowUtils.throwIf(questionVOS.isEmpty(), "题目不存在");
        QuestionVO questionVO = CollectionUtil.getFirst(questionVOS);
        // 获取对手信息
        Long targetId = userId.equals(match.getUserId1()) ? match.getUserId2() : match.getUserId1();
        User user = userCache.get(targetId);
        UserShowVO userShowVO = UserAdapter.buildUserShowVO(user);
        PkMatchStartVO startVO = PkMatchStartVO.builder()
                .matchVO(matchVO)
                .questionVO(questionVO)
                .userShowVO(userShowVO)
                .build();
        return startVO;
    }

    /**
     * 取消匹配
     *
     * @param userId
     * @return
     */
    @Override
    public boolean cancelMatch(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.MATCH_PK_FIND_SET);
        Long aLong = RedisUtils.setRemove(key, userId);
        return true;
    }

    @Override
    public PageUtils getPkRecordByUser(PageRequest pageRequest, Long userId) {
        Page<OnlinePkMatch> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        QueryWrapper<OnlinePkMatch> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .and(we -> {
                    we.eq(OnlinePkMatch::getUserId1, userId)
                            .or()
                            .eq(OnlinePkMatch::getUserId2, userId);
                })
                .eq(OnlinePkMatch::getStatus, MatchStatusEnum.FINISHED.getCode())
                .orderByDesc(OnlinePkMatch::getCreateTime);
        Page<OnlinePkMatch> page1 = matchOnlinepkService.page(page, wrapper);
        List<OnlinePkMatch> records = page1.getRecords();
        List<PkRecordVO> collect = records.stream().map(item -> {
            OnlinePKResultVO onlinePKResultVO = buildOnlinePKResultVO(item);
            Question question = questionService.getById(item.getQuestionId());
            PkRecordVO pkRecordVO = new PkRecordVO();
            pkRecordVO.setResultVO(onlinePKResultVO);
            pkRecordVO.setStartTime(item.getStartTime());
            pkRecordVO.setQuestionName(question.getTitle());
            return pkRecordVO;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page1);
        pageUtils.setList(collect);
        return pageUtils;
    }
}
