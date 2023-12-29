package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.mapper.UserFriendMapper;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.UserApply;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.UserFriendService;


@Service("userFriendService")
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements UserFriendService {

    @Autowired
    UserFriendMapper userFriendMapper;

    /**
     * 获取好友记录
     *
     * @param userId
     * @param targetUserId
     * @return
     */
    @Override
    public UserFriend getByFriend(Long userId, Long targetUserId) {
        UserFriend userFriend = lambdaQuery().eq(UserFriend::getUserId, userId)
                .eq(UserFriend::getFriendId, targetUserId).one();
        return userFriend;
    }

    /**
     * 创建好友关系
     *
     * @param userId
     * @param targetId
     */
    @Override
    public void createFriendRelate(Long userId, Long targetId) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUserId(userId);
        userFriend1.setFriendId(targetId);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUserId(targetId);
        userFriend2.setFriendId(userId);
        this.saveBatch(Lists.newArrayList(userFriend1, userFriend2));
    }


    //    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<UserFriendEntity> page = this.page(
//                new Query<UserFriendEntity>().getPage(params),
//                new QueryWrapper<UserFriendEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}
