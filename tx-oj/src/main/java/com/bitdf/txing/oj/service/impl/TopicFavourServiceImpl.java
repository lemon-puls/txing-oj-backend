package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.TopicFavourMapper;
import com.bitdf.txing.oj.model.entity.forum.TopicFavour;
import com.bitdf.txing.oj.service.TopicFavourService;
import com.bitdf.txing.oj.utils.page.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("topicFavourService")
public class TopicFavourServiceImpl extends ServiceImpl<TopicFavourMapper, TopicFavour> implements TopicFavourService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<TopicFavourEntity> page = this.page(
//                new Query<TopicFavourEntity>().getPage(params),
//                new QueryWrapper<TopicFavourEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}