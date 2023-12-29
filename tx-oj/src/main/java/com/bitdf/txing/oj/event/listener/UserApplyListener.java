package com.bitdf.txing.oj.event.listener;

import com.bitdf.txing.oj.event.UserApplyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Lizhiwei
 * @date 2023/12/29 10:43:39
 * 注释：
 */
@Component
@Slf4j
public class UserApplyListener {

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        log.info("[处理好友申请事件]：userId: {} targetId: {}", event.getUserApply().getUserId(), event.getUserApply().getTargetId());
    }
}
