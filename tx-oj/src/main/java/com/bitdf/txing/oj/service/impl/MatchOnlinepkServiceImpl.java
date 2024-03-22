package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.MatchOnlinepkMapper;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;
import com.bitdf.txing.oj.model.enume.match.MatchStatusEnum;
import com.bitdf.txing.oj.service.MatchOnlinepkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("matchOnlinepkService")
public class MatchOnlinepkServiceImpl extends ServiceImpl<MatchOnlinepkMapper, OnlinePkMatch> implements MatchOnlinepkService {

    @Autowired
    MatchOnlinepkMapper matchOnlinepkMapper;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MatchOnlinepkEntity> page = this.page(
//                new Query<MatchOnlinepkEntity>().getPage(params),
//                new QueryWrapper<MatchOnlinepkEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    /**
     * @param userId
     * @return
     */
    @Override
    public OnlinePkMatch getRunningByUserId(Long userId) {
        QueryWrapper<OnlinePkMatch> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(OnlinePkMatch::getStatus, MatchStatusEnum.RUNNING.getCode())
                .and(wrapper1 -> wrapper1.eq(OnlinePkMatch::getUserId1, userId)
                        .or()
                        .eq(OnlinePkMatch::getUserId2, userId));
        return this.getOne(wrapper);
    }

    /**
     * 保存结束时间 单方面
     *
     * @param matchId
     * @param userId
     */
    @Override
    public void finishMatch(Long matchId, Long userId) {
        matchOnlinepkMapper.finishMatch(matchId, userId);
    }

    @Override
    public OnlinePkMatch isFinished(Long matchId) {
        QueryWrapper<OnlinePkMatch> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .isNotNull(OnlinePkMatch::getSubmitTime1)
                .isNotNull(OnlinePkMatch::getSubmitTime2)
                .eq(OnlinePkMatch::getStatus, MatchStatusEnum.RUNNING.getCode());
        return this.getOne(wrapper);
    }

    @Override
    public void updateMatchStatus(Long matchId, Integer code) {
        OnlinePkMatch onlinePkMatch = new OnlinePkMatch();
        onlinePkMatch.setStatus(code);
        onlinePkMatch.setId(matchId);
        this.updateById(onlinePkMatch);
    }

    @Override
    public List<OnlinePkMatch> getMatchsByUserId(Long userId) {
        List<OnlinePkMatch> matchs = matchOnlinepkMapper.getMatchsByUserId(userId);
        return matchs;
    }
}