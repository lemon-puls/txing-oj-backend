package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.mapper.UserEmojiMapper;
import com.bitdf.txing.oj.model.entity.user.UserEmoji;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.UserEmojiService;


@Service("userEmojiService")
public class UserEmojiServiceImpl extends ServiceImpl<UserEmojiMapper, UserEmoji> implements UserEmojiService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<UserEmojiEntity> page = this.page(
//                new Query<UserEmojiEntity>().getPage(params),
//                new QueryWrapper<UserEmojiEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}
