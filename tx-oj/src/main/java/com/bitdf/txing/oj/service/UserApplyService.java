package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.UserApply;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.user.FriendApplyVO;

import java.util.Date;

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

    CursorPageBaseVO<FriendApplyVO> getPageByCursor(CursorPageBaseRequest cursorPageBaseRequest, Long id);

    Integer getUnReadApplyCount(Long targetId);

    void markReadFriendApply(Long userId, Date date);

//    PageUtils queryPage(Map<String, Object> params);
}

