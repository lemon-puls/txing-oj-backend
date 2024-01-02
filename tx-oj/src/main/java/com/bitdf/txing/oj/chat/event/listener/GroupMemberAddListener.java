package com.bitdf.txing.oj.chat.event.listener;

import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsGroupMemberChangeVO;
import com.bitdf.txing.oj.chat.event.GroupMemberAddEvent;
import com.bitdf.txing.oj.chat.service.adapter.MemberAdapter;
import com.bitdf.txing.oj.chat.service.adapter.MessageAdapter;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.chat.service.cache.GroupMemberCache;
import com.bitdf.txing.oj.model.entity.chat.GroupMember;
import com.bitdf.txing.oj.model.entity.chat.RoomGroup;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2024/1/2 13:06:50
 * 注释：
 */
@Slf4j
@Component
public class GroupMemberAddListener {

    @Autowired
    UserCache userCache;
    @Autowired
    ChatService chatService;
    @Autowired
    GroupMemberCache groupMemberCache;
    @Autowired
    UserService userService;
    @Autowired
    PushService pushService;

    /**
     * 给群聊全员发送“。。。邀请。。。加入群聊”消息
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent event) {
        RoomGroup roomGroup = event.getRoomGroup();
        List<GroupMember> memberList = event.getMemberList();
        Long inviteUserId = event.getInviteUserId();
        User inviteUser = userCache.get(inviteUserId);
        Map<Long, User> newMemberMap = userCache.getBatch(memberList.stream()
                .map(GroupMember::getUserId).collect(Collectors.toList()));
        ChatMessageRequest chatMessageRequest = MessageAdapter.buildGroupMemberAddMessage(roomGroup, inviteUser, newMemberMap);
        chatService.sendMsg(chatMessageRequest, User.SYSTEM_USER_ID);
    }

    /**
     * 给群聊中原有成员推送ws变动通知
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendGroupMemberChangePush(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUserIdList = groupMemberCache.getMemberUserIdList(roomGroup.getRoomId());
        List<Long> newMemberIds = memberList.stream().map(GroupMember::getUserId).collect(Collectors.toList());
        List<User> users = userService.listByIds(newMemberIds);
        users.forEach(user -> {
            WsBaseVO<WsGroupMemberChangeVO> wsBaseVO = MemberAdapter.buildGroupMemberAddWs(roomGroup.getRoomId(), user);
            pushService.sendPushMsg(wsBaseVO, memberUserIdList, null);
        });
        // 移除群聊成员缓存
        groupMemberCache.evictMemberIdList(roomGroup.getRoomId());
    }
}
