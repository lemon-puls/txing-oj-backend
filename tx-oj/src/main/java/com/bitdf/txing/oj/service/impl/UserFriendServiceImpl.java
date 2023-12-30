package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.mapper.UserFriendMapper;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.entity.user.UserApply;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.user.FriendVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.adapter.FriendAdapter;
import com.bitdf.txing.oj.utils.CursorUtils;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.UserFriendService;


@Service("userFriendService")
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements UserFriendService {

    @Autowired
    UserFriendMapper userFriendMapper;
    @Autowired
    UserService userService;

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

    /**
     * 游标翻页
     * @param userId
     * @param cursorPageBaseRequest
     * @return
     */
    @Override
    public CursorPageBaseVO<FriendVO> cursorPage(Long userId, CursorPageBaseRequest cursorPageBaseRequest) {
        CursorPageBaseVO<UserFriend> friendPage = this.getFriendPage(userId, cursorPageBaseRequest);
        if (CollectionUtil.isEmpty(friendPage.getList())) {
            return CursorPageBaseVO.empty();
        }
        List<Long> friendIds = friendPage.getList().stream().map(UserFriend::getFriendId).collect(Collectors.toList());
        List<User> userList = userService.getFriendsByIds(friendIds);
        List<FriendVO> friendVOS = FriendAdapter.buildFriend(friendPage.getList(), userList);
        CursorPageBaseVO res = CursorPageBaseVO.init(friendPage, friendVOS);
        return res;
    }

    /**
     * 获取下一页（游标翻页）
     * @param userId
     * @param cursorPageBaseRequest
     * @return
     */
    @Override
    public CursorPageBaseVO<UserFriend> getFriendPage(Long userId, CursorPageBaseRequest cursorPageBaseRequest) {
        CursorPageBaseVO<UserFriend> pageBaseVO = CursorUtils.getCursorPageByMysql(this,
                cursorPageBaseRequest, wrapper -> wrapper.eq(UserFriend::getUserId, userId), UserFriend::getCreateTime);
        return pageBaseVO;
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
