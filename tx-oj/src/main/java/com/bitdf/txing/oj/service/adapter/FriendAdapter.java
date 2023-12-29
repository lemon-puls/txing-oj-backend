package com.bitdf.txing.oj.service.adapter;

import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.UserApply;
import com.bitdf.txing.oj.model.enume.UserApplyReadStatusEnum;
import com.bitdf.txing.oj.model.enume.UserApplyStatusEnum;
import com.bitdf.txing.oj.model.enume.UserApplyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lizhiwei
 * @date 2023/12/29 9:51:24
 * 注释：好友适配器
 */
public class FriendAdapter {

    /**
     * 构建userApply
     * @param userId
     * @param request
     * @return
     */
    public static UserApply buildFriendApply(Long userId, UserApplyRequest request) {
        UserApply userApply = new UserApply();
        userApply.setUserId(userId);
        userApply.setMsg(request.getMsg());
        userApply.setTargetId(request.getTargetUserId());
        userApply.setStatus(UserApplyStatusEnum.WAITTING.getCode());
        userApply.setReadStatus(UserApplyReadStatusEnum.UNREAD.getCode());
        userApply.setType(UserApplyTypeEnum.ADD_FRIEND.getCode());
        return userApply;
    }
}
