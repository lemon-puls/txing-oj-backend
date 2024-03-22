package com.bitdf.txing.oj.chat.service.adapter;

import com.bitdf.txing.oj.chat.domain.vo.response.*;
import com.bitdf.txing.oj.chat.enume.WsRespTypeEnum;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.UserActiveStatusEnum;
import com.bitdf.txing.oj.model.vo.match.WsOnlinePkTeamUpVO;
import com.bitdf.txing.oj.model.vo.user.WsFriendApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:15:51
 * 注释：
 */
@Component
public class WsAdapter {

    @Autowired
    ChatService chatService;

    public static WsBaseVO<ChatMessageVO> buildMsgSend(ChatMessageVO chatMessageVO) {
        WsBaseVO<ChatMessageVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setType(WsRespTypeEnum.MESSAGE.getType());
        wsBaseVO.setData(chatMessageVO);
        return wsBaseVO;
    }

    public static WsBaseVO<WsOnlinePkTeamUpVO> buildPKTeamUpNotifyVO(WsOnlinePkTeamUpVO wsOnlinePkTeamUpVO) {
        WsBaseVO<WsOnlinePkTeamUpVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setType(WsRespTypeEnum.ONLINE_PK_TEAM_UP_NOTIFY.getType());
        wsBaseVO.setData(wsOnlinePkTeamUpVO);
        return wsBaseVO;
    }

    public static WsBaseVO<?> buildInvalidTokenWsVO() {
        WsBaseVO wsBaseVO = new WsBaseVO();
        wsBaseVO.setType(WsRespTypeEnum.USER_TOKEN_INVALID.getType());
        return wsBaseVO;
    }

    public static WsBaseVO<WsLoginSuccessVO> buildLoginSuccessVO(User user, String token) {
        WsBaseVO<WsLoginSuccessVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setType(WsRespTypeEnum.USER_LOGIN_SUCCESS.getType());
        WsLoginSuccessVO build = WsLoginSuccessVO.builder()
                .avatar(user.getUserAvatar())
                .token(token)
                .userName(user.getUserName())
                .userId(user.getId())
                .build();
        wsBaseVO.setData(build);
        return wsBaseVO;
    }

    public static WsBaseVO<WsFriendApplyVO> buildWsUserApply(WsFriendApplyVO wsFriendApplyVO) {
        WsBaseVO<WsFriendApplyVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setType(WsRespTypeEnum.FRIEND_APPLY_NOTIFY.getType());
        wsBaseVO.setData(wsFriendApplyVO);
        return wsBaseVO;
    }

    public static WsBaseVO<WsFriendApplyAgreeVO> buildFriendApplyAgreeWs(WsFriendApplyAgreeVO applyAgreeVO) {
        WsBaseVO<WsFriendApplyAgreeVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setData(applyAgreeVO);
        wsBaseVO.setType(WsRespTypeEnum.FRIEND_APPLY_AGREE_NOTIFY.getType());
        return wsBaseVO;
    }

    public WsBaseVO<WsOnlineOfflineNotifyVO> buildOffLineNotifyWsVO(User user, Long userId) {
        WsBaseVO<WsOnlineOfflineNotifyVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setType(WsRespTypeEnum.USER_ONLINE_OFFLINE_NOTIFY.getType());
        WsOnlineOfflineNotifyVO offlineNotifyVO = WsOnlineOfflineNotifyVO.builder()
                .chatMemberVOS(Collections.singletonList(buildOfflineChatMember(user)))
                .onlineNum(getChatMemberStatisticVO().getOnlineNum())
                .build();
        wsBaseVO.setData(offlineNotifyVO);
        return wsBaseVO;
    }

    private ChatMemberStatisticVO getChatMemberStatisticVO() {
        return chatService.getChatMemberStatisticVO();
    }

    private static ChatMemberVO buildOfflineChatMember(User user) {
        return ChatMemberVO.builder()
                .lastOpsTime(user.getLastOpsTime())
                .activeStatus(UserActiveStatusEnum.OFFLINE.getCode())
                .userId(user.getId())
                .build();
    }

    public WsBaseVO<WsOnlineOfflineNotifyVO> buildOnlineNotifyWsVO(User user) {
        WsBaseVO<WsOnlineOfflineNotifyVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setType(WsRespTypeEnum.USER_ONLINE_OFFLINE_NOTIFY.getType());
        WsOnlineOfflineNotifyVO onlineNotifyVO = new WsOnlineOfflineNotifyVO();
        onlineNotifyVO.setChatMemberVOS(Collections.singletonList(buildOnlineChatMember(user)));
        onlineNotifyVO.setOnlineNum(getChatMemberStatisticVO().getOnlineNum());
        wsBaseVO.setData(onlineNotifyVO);
        return wsBaseVO;
    }

    private ChatMemberVO buildOnlineChatMember(User user) {
        return ChatMemberVO.builder()
                .lastOpsTime(user.getLastOpsTime())
                .activeStatus(UserActiveStatusEnum.ONLINE.getCode())
                .userId(user.getId())
                .build();
    }
}
