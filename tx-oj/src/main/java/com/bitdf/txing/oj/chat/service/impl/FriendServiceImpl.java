package com.bitdf.txing.oj.chat.service.impl;

import com.bitdf.txing.oj.chat.service.FriendService;
import com.bitdf.txing.oj.model.entity.chat.Friend;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.mapper.FriendMapper;


@Service("friendService")
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<FriendEntity> page = this.page(
//                new Query<FriendEntity>().getPage(params),
//                new QueryWrapper<FriendEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}
