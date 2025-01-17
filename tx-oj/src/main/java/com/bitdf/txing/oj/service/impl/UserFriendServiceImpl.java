package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.service.business.RoomAppService;
import com.bitdf.txing.oj.chat.service.cache.RoomCache;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.mapper.UserFriendMapper;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.user.FriendVO;
import com.bitdf.txing.oj.service.UserFriendService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.adapter.FriendAdapter;
import com.bitdf.txing.oj.utils.CursorUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service("userFriendService")
@Slf4j
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements UserFriendService {

    @Autowired
    UserFriendMapper userFriendMapper;
    @Autowired
    UserService userService;
    @Autowired
    RoomAppService roomAppService;
    @Autowired
    RoomCache roomCache;

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
     *
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
     *
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


    /**
     * 删除好友
     *
     * @param userId
     * @param friendId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long userId, Long friendId) {
        List<UserFriend> userFriends = this.getUserFriend(userId, friendId);
        if (CollectionUtil.isEmpty(userFriends)) {
            log.info("[删除好友] --> 无需删除 不存在好友关系");
            throw new BusinessException(TxCodeEnume.COMMON_CUSTOM_EXCEPTION, "当前你们不是好友关系！");
        }
        List<Long> entityIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        this.removeByIds(entityIds);
        // 禁用房间
//        roomAppService.deleteFriendRoom(Arrays.asList(userId, friendId));
        Long roomId = roomAppService.disableRoom(Arrays.asList(userId, friendId));
        // 删除缓存
        roomCache.delete(roomId);
    }

    private List<UserFriend> getUserFriend(Long userId, Long friendId) {
        return lambdaQuery()
                .eq(UserFriend::getUserId, userId)
                .eq(UserFriend::getFriendId, friendId)
                .or()
                .eq(UserFriend::getFriendId, userId)
                .eq(UserFriend::getUserId, friendId)
                .select(UserFriend::getId)
                .list();
    }
}
