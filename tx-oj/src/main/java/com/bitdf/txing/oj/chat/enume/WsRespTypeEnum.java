package com.bitdf.txing.oj.chat.enume;

import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsGroupMemberChangeVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsLoginSuccessVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsOnlineOfflineNotifyVO;
import com.bitdf.txing.oj.model.vo.user.WsFriendApplyVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:30:21
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum WsRespTypeEnum {

    MESSAGE(0, "新消息", ChatMessageVO.class),
    GROUP_MEMBER_CHANGE(1, "群聊成员变动", WsGroupMemberChangeVO.class),
    USER_ONLINE_OFFLINE_NOTIFY(2, "上下线通知", WsOnlineOfflineNotifyVO.class),
    USER_TOKEN_INVALID(3, "用户Token失效", null),
    USER_LOGIN_SUCCESS(4, "用户登录成功", WsLoginSuccessVO.class),
    FRIEND_APPLY_NOTIFY(5, "好友申请通知", WsFriendApplyVO.class);

    private final Integer type;
    private final String desc;
    private final Class dataClass;
}
