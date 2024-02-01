package com.bitdf.txing.oj.chat.event.listener;

import com.bitdf.txing.oj.chat.event.UserOnlineEvent;
import com.bitdf.txing.oj.chat.service.adapter.WsAdapter;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.UserActiveStatusEnum;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserCache;
import com.bitdf.txing.oj.service.cache.UserRelateCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2024/1/7 22:41:40
 * 注释：
 */
@Slf4j
@Component
public class UserOnlineListener {

    @Autowired
    UserRelateCache userRelateCache;
    @Autowired
    PushService pushService;
    @Autowired
    WsAdapter wsAdapter;
    @Autowired
    UserService userService;
    @Autowired
    UserCache userCache;


    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveRedisAndPush(UserOnlineEvent event) {
        User user = event.getUser();
        userRelateCache.online(user.getId(), user.getLastOpsTime());
        // 通知所有在线用户 该用户上线了
        pushService.sendPushMsg(wsAdapter.buildOnlineNotifyWsVO(event.getUser()), null);
    }

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOpsTime(user.getLastOpsTime());
        update.setActiveStatus(UserActiveStatusEnum.ONLINE.getCode());
        userService.updateById(update);
        userCache.delete(user.getId());
        userRelateCache.refreshUserModifyTime(user.getId());
        log.info("执行了saveDB");
    }

}
