package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.MatchSubmitRelateMapper;
import com.bitdf.txing.oj.model.entity.match.MatchSubmitRelate;
import com.bitdf.txing.oj.service.MatchSubmitRelateService;
import org.springframework.stereotype.Service;


@Service("matchSubmitRelateService")
public class MatchSubmitRelateServiceImpl extends ServiceImpl<MatchSubmitRelateMapper, MatchSubmitRelate> implements MatchSubmitRelateService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MatchSubmitRelateEntity> page = this.page(
//                new Query<MatchSubmitRelateEntity>().getPage(params),
//                new QueryWrapper<MatchSubmitRelateEntity>()
//        );
//
//        return new PageUtils(page);
//    }
}