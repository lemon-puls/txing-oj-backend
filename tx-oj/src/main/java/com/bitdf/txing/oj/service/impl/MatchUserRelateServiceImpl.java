package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.MatchUserRelateMapper;
import com.bitdf.txing.oj.model.entity.match.MatchUserRelate;
import com.bitdf.txing.oj.service.MatchUserRelateService;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("matchUserRelateService")
public class MatchUserRelateServiceImpl extends ServiceImpl<MatchUserRelateMapper, MatchUserRelate> implements MatchUserRelateService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MatchUserRelateEntity> page = this.page(
//                new Query<MatchUserRelateEntity>().getPage(params),
//                new QueryWrapper<MatchUserRelateEntity>()
//        );
//
//        return new PageUtils(page);
//    }

    @Override
    public MatchUserRelate getByMatchIdAndUserId(Long matchId, Long userId) {
        MatchUserRelate matchUserRelate = this.getOne(new QueryWrapper<MatchUserRelate>().lambda()
                .eq(MatchUserRelate::getMatchId, matchId)
                .eq(MatchUserRelate::getUserId, userId));
        return matchUserRelate;
    }

    @Override
    public MatchUserRelate getLastJoinRecord(Long matchId, Long userId) {
        MatchUserRelate matchUserRelate = this.getOne(new QueryWrapper<MatchUserRelate>().lambda()
                .eq(MatchUserRelate::getMatchId, matchId)
                .eq(MatchUserRelate::getUserId, userId)
                .select(MatchUserRelate::getId)
                .orderByDesc(MatchUserRelate::getCreateTime)
                .last("limit 1"));
        return matchUserRelate;
    }

    @Override
    public void saveEndTime(Long matchId, Long userId) {
        boolean update = this.update(new UpdateWrapper<MatchUserRelate>().lambda()
                .eq(MatchUserRelate::getMatchId, matchId)
                .eq(MatchUserRelate::getUserId, userId)
                .eq(MatchUserRelate::getEndTime, null)
                .set(MatchUserRelate::getEndTime, new Date()));
    }
}