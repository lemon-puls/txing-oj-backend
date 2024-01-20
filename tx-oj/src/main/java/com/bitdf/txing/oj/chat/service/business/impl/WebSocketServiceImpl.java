package com.bitdf.txing.oj.chat.service.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.bitdf.txing.oj.chat.domain.dto.WsAuthorize;
import com.bitdf.txing.oj.chat.domain.dto.WsChannelExtraDTO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsLoginSuccessVO;
import com.bitdf.txing.oj.chat.event.UserOffLineEvent;
import com.bitdf.txing.oj.chat.event.UserOnlineEvent;
import com.bitdf.txing.oj.chat.service.adapter.WsAdapter;
import com.bitdf.txing.oj.chat.service.business.WebSocketService;
import com.bitdf.txing.oj.chat.websocket.NettyUtil;
import com.bitdf.txing.oj.config.ThreadPoolConfig;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserRelateCache;
import com.bitdf.txing.oj.utils.UserTokenUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Lizhiwei
 * @date 2024/1/2 19:18:23
 * 注释：
 */
@Component
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    ThreadPoolTaskExecutor websocketExecutor;
    @Autowired
    UserService userService;
    @Autowired
    UserRelateCache userRelateCache;

    /**
     * 已连接的websocket连接 ===》 一些额外的参数
     *
     * @param channel
     */
    public static final ConcurrentHashMap<Channel, WsChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 在线用户 ===》 对应的channel集合
     *
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
            log.info("用户{}已经完全下线 触发下线事件", optional.get());
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

    /**
     * @param wsBaseVO
     * @param userId
     */
    @Override
    public void sendToAllOnline(WsBaseVO<?> wsBaseVO, Long userId) {
        ONLINE_WS_MAP.forEach(((channel, wsChannelExtraDTO) -> {
            if (Objects.nonNull(userId) && Objects.equals(wsChannelExtraDTO.getUserId(), userId)) {
                return;
            }
            websocketExecutor.execute(() -> sendMsg(channel, wsBaseVO));

        }));

    }

    /**
     * 发送系哦啊剖析
     *
     * @param channel
     * @param wsBaseVO
     */
    private void sendMsg(Channel channel, WsBaseVO<?> wsBaseVO) {
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseVO));
        channel.writeAndFlush(textWebSocketFrame);
    }

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WsChannelExtraDTO());
    }

    /**
     * ws连接建立完成后通过此方法判断是否已登录 以及 进行相应的处理
     *
     * @param channel
     * @param wsAuthorize
     */
    @Override
    public void authorize(Channel channel, WsAuthorize wsAuthorize) {
        // TODO 校验是否已登录
        boolean isLogin = UserTokenUtils.isLogin(wsAuthorize.getUserId());
        if (isLogin) {
            User user = userService.getById(wsAuthorize.getUserId());
            alreadyLogin(channel, user, wsAuthorize.getToken());
        } else {
            // 通知前端重新登录（使前端token失效）
            sendMsg(channel, WsAdapter.buildInvalidTokenWsVO());
        }
    }


    private void alreadyLogin(Channel channel, User user, String token) {
        // 更新在线列表
        online(channel, user.getId());
        // 通知该用户自己 已登录成功
        WsBaseVO<WsLoginSuccessVO> wsBaseVO = WsAdapter.buildLoginSuccessVO(user, token);
        sendMsg(channel, wsBaseVO);
        // 如果用户是刚上线 就触发用户上线事件
        boolean isOnline = userRelateCache.isOnline(user.getId());
        if (!isOnline) {
            user.setLastOpsTime(new Date());
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    private void online(Channel channel, Long userId) {
        WsChannelExtraDTO wsChannelExtraDTO = getInitChannelExt(channel);
        wsChannelExtraDTO.setUserId(userId);

        ONLINE_USERID_MAP.putIfAbsent(userId, new CopyOnWriteArrayList<>());
        ONLINE_USERID_MAP.get(userId).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.USERID, userId);
    }

    private WsChannelExtraDTO getInitChannelExt(Channel channel) {
        WsChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.getOrDefault(channel, new WsChannelExtraDTO());
        WsChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    @Override
    public void sendToUserId(WsBaseVO<?> wsBaseVO, Long userId) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_USERID_MAP.get(userId);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户{}: 不在线，无需推送！", userId);
            return;
        }
        for (Channel channel : channels) {
            websocketExecutor.execute(() -> sendMsg(channel, wsBaseVO));
        }
    }
}
