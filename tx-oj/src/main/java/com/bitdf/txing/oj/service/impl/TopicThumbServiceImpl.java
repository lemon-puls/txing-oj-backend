package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.TopicThumbMapper;
import com.bitdf.txing.oj.model.entity.forum.TopicThumb;
import com.bitdf.txing.oj.service.TopicThumbService;
import org.springframework.stereotype.Service;


@Service("topicThumbService")
public class TopicThumbServiceImpl extends ServiceImpl<TopicThumbMapper, TopicThumb> implements TopicThumbService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<TopicThumbEntity> page = this.page(
//                new Query<TopicThumbEntity>().getPage(params),
//                new QueryWrapper<TopicThumbEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}