package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.MatchUserRelateMapper;
import com.bitdf.txing.oj.model.entity.match.MatchUserRelate;
import com.bitdf.txing.oj.model.enume.match.MatchJoinTypeEnum;
import com.bitdf.txing.oj.model.enume.match.MatchUserJudgeStatusEnum;
import com.bitdf.txing.oj.model.enume.match.MatchUserStatusEnum;
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
                .orderByDesc(MatchUserRelate::getCreateTime)
                .last("limit 1"));
        return matchUserRelate;
    }

    @Override
    public Long saveEndTime(Long matchId, Long userId) {
        LambdaQueryWrapper<MatchUserRelate> wrapper = new QueryWrapper<MatchUserRelate>().lambda()
                .eq(MatchUserRelate::getMatchId, matchId)
                .eq(MatchUserRelate::getUserId, userId)
                .eq(MatchUserRelate::getStatus, MatchUserStatusEnum.NORMAL.getCode())
                .isNull(MatchUserRelate::getEndTime);
        MatchUserRelate matchUserRelate = this.getOne(wrapper);
        MatchUserRelate update = new MatchUserRelate();
        update.setId(matchUserRelate.getId());
        update.setEndTime(new Date());
        boolean b = this.updateById(update);
        return matchUserRelate.getId();
    }

    @Override
    public MatchUserRelate getSimulateMatchRunning(Long userId) {
        QueryWrapper<MatchUserRelate> wrapper = new QueryWrapper<>();
        Date date = new Date();
        wrapper.lambda()
                .eq(MatchUserRelate::getUserId, userId)
                .eq(MatchUserRelate::getJudgeStatus, MatchUserJudgeStatusEnum.WAITTING.getCode())
                .eq(MatchUserRelate::getJoinType, MatchJoinTypeEnum.SIMULATE.getCode())
                .eq(MatchUserRelate::getStatus, MatchUserStatusEnum.NORMAL.getCode())
                .le(MatchUserRelate::getEndTime, date)
                .ge(MatchUserRelate::getStartTime, date)
                .orderByDesc(MatchUserRelate::getStartTime)
                .last("limit 1");
        return this.getOne(wrapper);
    }

    @Override
    public void updateUserStatus(Integer code, Long userId, Long matchId) {
        LambdaUpdateWrapper<MatchUserRelate> wrapper = new UpdateWrapper<MatchUserRelate>().lambda()
                .eq(MatchUserRelate::getUserId, userId)
                .eq(MatchUserRelate::getMatchId, matchId)
                .isNull(MatchUserRelate::getEndTime)
                .eq(MatchUserRelate::getStatus, MatchUserStatusEnum.NORMAL.getCode())
                .set(MatchUserRelate::getStatus, MatchUserStatusEnum.GIVEUP.getCode())
                .set(MatchUserRelate::getEndTime, new Date());
        this.update(wrapper);
    }
}