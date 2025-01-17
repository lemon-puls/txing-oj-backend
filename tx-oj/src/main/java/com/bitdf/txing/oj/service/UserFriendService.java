package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.user.FriendVO;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 23:46:51
 */
public interface UserFriendService extends IService<UserFriend> {
    UserFriend getByFriend(Long userId, Long targetUserId);

    void createFriendRelate(Long userId, Long targetId);

    CursorPageBaseVO<FriendVO> cursorPage(Long userId, CursorPageBaseRequest cursorPageBaseRequest);

    CursorPageBaseVO<UserFriend> getFriendPage(Long userId, CursorPageBaseRequest cursorPageBaseRequest);

    void deleteFriend(Long userId, Long friendId);

//    PageUtils queryPage(Map<String, Object> params);
}

