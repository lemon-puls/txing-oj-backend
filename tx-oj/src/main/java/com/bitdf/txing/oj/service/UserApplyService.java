package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.UserApply;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 23:46:51
 */
public interface UserApplyService extends IService<UserApply> {
    void applyFriend(Long userId, UserApplyRequest request);

    void agreeApply(Long userId, Long applyId);

//    PageUtils queryPage(Map<String, Object> params);
}

