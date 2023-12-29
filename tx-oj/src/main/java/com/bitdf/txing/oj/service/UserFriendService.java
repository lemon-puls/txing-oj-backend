package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.utils.page.PageUtils;

import java.util.Map;

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

//    PageUtils queryPage(Map<String, Object> params);
}

