package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.MatchWeekMapper;
import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.vo.match.WeekMatchVO;
import com.bitdf.txing.oj.service.MatchWeekService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Service("matchWeekService")
public class MatchWeekServiceImpl extends ServiceImpl<MatchWeekMapper, WeekMatch> implements MatchWeekService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MatchWeekEntity> page = this.page(
//                new Query<MatchWeekEntity>().getPage(params),
//                new QueryWrapper<MatchWeekEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public WeekMatch getLastSessionMatch() {
        WeekMatch weekMatch = this.lambdaQuery()
                .orderByDesc(WeekMatch::getSessionNo)
                .last("limit 1").one();
        return weekMatch;
    }

    @Override
    public WeekMatchVO getNextMatch() {
        QueryWrapper<WeekMatch> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .orderByDesc(WeekMatch::getStartTime)
                .last("limit 1");
        WeekMatch weekMatch = this.getOne(wrapper);
        WeekMatchVO weekMatchVO = new WeekMatchVO();
        BeanUtils.copyProperties(weekMatch, weekMatchVO);
        return weekMatchVO;
    }
}