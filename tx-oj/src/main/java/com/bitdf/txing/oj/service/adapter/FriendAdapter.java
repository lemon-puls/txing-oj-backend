package com.bitdf.txing.oj.service.adapter;

import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.entity.user.UserApply;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.model.enume.UserApplyReadStatusEnum;
import com.bitdf.txing.oj.model.enume.UserApplyStatusEnum;
import com.bitdf.txing.oj.model.enume.UserApplyTypeEnum;
import com.bitdf.txing.oj.model.vo.user.FriendVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/29 9:51:24
 * 注释：好友适配器
 */
public class FriendAdapter {

    /**
     * 构建userApply
     *
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

    /**
     * @param list
     * @param userList
     * @return
     */
    public static List<FriendVO> buildFriend(List<UserFriend> list, List<User> userList) {
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        return list.stream().map((friend) -> {
            FriendVO friendVO = new FriendVO();
            friendVO.setUserId(friend.getFriendId());
            User user = userMap.get(friend.getFriendId());
            if (user != null) {
                friendVO.setActiveStatus(user.getActiveStatus());
            }
            return friendVO;
        }).collect(Collectors.toList());
    }
}