package com.bitdf.txing.oj.chat.event.listener;

import com.bitdf.txing.oj.chat.event.UserOffLineEvent;
import com.bitdf.txing.oj.chat.service.adapter.WsAdapter;
import com.bitdf.txing.oj.chat.service.business.WebSocketService;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.UserActiveStatusEnum;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserRelateCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2024/1/2 22:34:34
 * 注释：
 */
@Slf4j
@Component
public class UserOffLineListener {

    @Autowired
    UserRelateCache userRelateCache;
    @Autowired
    WebSocketService webSocketService;
    @Autowired
    WsAdapter wsAdapter;
    @Autowired
    UserService userService;

    @Async
    @EventListener(classes = UserOffLineEvent.class)
    public void saveRedisAndPush(UserOffLineEvent event) {
        log.info("触发saveRedisAndPush");
        User user = event.getUser();
        userRelateCache.offLine(user.getId(),user.getLastOpsTime());
        // 推送给所有用户
        webSocketService.sendToAllOnline(wsAdapter.buildOffLineNotifyWsVO(event.getUser(), event.getUser().getId()),
                event.getUser().getId());
    }

    @Async
    @EventListener(classes = UserOffLineEvent.class)
    public void saveDB(UserOffLineEvent event) {
        log.info("触发saveDB");
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOpsTime(user.getLastOpsTime());
        update.setActiveStatus(UserActiveStatusEnum.OFFLINE.getCode());
        userService.updateById(update);
    }
}
