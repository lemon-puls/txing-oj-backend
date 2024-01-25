package com.bitdf.txing.oj.event.listener;

import com.bitdf.txing.oj.chat.service.adapter.WsAdapter;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.chat.service.business.WebSocketService;
import com.bitdf.txing.oj.event.UserApplyEvent;
import com.bitdf.txing.oj.model.entity.user.UserApply;
import com.bitdf.txing.oj.service.UserApplyService;
import com.bitdf.txing.oj.service.adapter.FriendAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Arrays;

/**
 * @author Lizhiwei
 * @date 2023/12/29 10:43:39
 * 注释：
 */
@Component
@Slf4j
public class UserApplyListener {

    @Autowired
    UserApplyService userApplyService;
    @Autowired
    WebSocketService webSocketService;
    @Autowired
    PushService pushService;

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        log.info("[处理好友申请事件]：userId: {} targetId: {}", event.getUserApply().getUserId(), event.getUserApply().getTargetId());
        UserApply userApply = event.getUserApply();
        Integer unReadCount = userApplyService.getUnReadApplyCount(userApply.getTargetId());
        pushService.sendPushMsg(
                WsAdapter.buildWsUserApply(FriendAdapter.buildWsUserApplyVO(userApply, unReadCount)),
                Arrays.asList(userApply.getTargetId()),
                null
        );
    }
}
