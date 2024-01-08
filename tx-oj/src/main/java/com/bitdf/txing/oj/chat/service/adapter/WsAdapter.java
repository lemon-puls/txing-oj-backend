package com.bitdf.txing.oj.chat.service.adapter;

import com.bitdf.txing.oj.chat.domain.vo.response.*;
import com.bitdf.txing.oj.chat.enume.WsRespTypeEnum;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.UserActiveStatusEnum;
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

    public static WsBaseVO<?> buildInvalidTokenWsVO() {
        WsBaseVO wsBaseVO = new WsBaseVO();
        wsBaseVO.setType(WsRespTypeEnum.USER_TOKEN_INVALID.getType());
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
