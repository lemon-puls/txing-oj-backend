package com.bitdf.txing.oj.chat.service.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.chat.domain.dto.WsChannelExtraDTO;
import com.bitdf.txing.oj.chat.event.UserOffLineEvent;
import com.bitdf.txing.oj.chat.service.business.WebSocketService;
import com.bitdf.txing.oj.model.entity.user.User;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Lizhiwei
 * @date 2024/1/2 19:18:23
 * 注释：
 */
@Component
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    /**
     * 已连接的websocket连接 ===》 一些额外的参数
     * @param channel
     */
    public static final ConcurrentHashMap<Channel, WsChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 在线用户 ===》 对应的channel集合
     * @param channel
     */
    public static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_USERID_MAP = new ConcurrentHashMap<>();



    @Override
    public void removed(Channel channel) {
        WsChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> optional = Optional.ofNullable(wsChannelExtraDTO).map(WsChannelExtraDTO::getUserId);
        Boolean flag = offline(channel, optional);
        if (optional.isPresent() && flag) {
            // 该用户已经完全下线 触发下线事件
            User user = new User();
            user.setId(optional.get());
            user.setLastOpsTime(new Date());
            applicationEventPublisher.publishEvent(new UserOffLineEvent(this, user));
        }
    }

    private Boolean offline(Channel channel, Optional<Long> optional) {
        ONLINE_WS_MAP.remove(channel);
        if (optional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_USERID_MAP.get(optional.get());
            if (!channels.isEmpty()) {
                channels.removeIf(channel1 -> channel1.equals(channel));
            }
            return CollectionUtil.isEmpty(channels);
        }
        return true;
    }
}
