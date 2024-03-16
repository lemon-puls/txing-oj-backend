package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.MatchOnlinepkMapper;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;
import com.bitdf.txing.oj.service.MatchOnlinepkService;
import org.springframework.stereotype.Service;


@Service("matchOnlinepkService")
public class MatchOnlinepkServiceImpl extends ServiceImpl<MatchOnlinepkMapper, OnlinePkMatch> implements MatchOnlinepkService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MatchOnlinepkEntity> page = this.page(
//                new Query<MatchOnlinepkEntity>().getPage(params),
//                new QueryWrapper<MatchOnlinepkEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}