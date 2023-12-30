package com.bitdf.txing.oj.chat.service.business.impl;

import com.bitdf.txing.oj.chat.constant.ChatMqConstant;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.utils.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:15:11
 * 注释：
 */
@Service
public class PushServiceImpl implements PushService {

    @Autowired
    MqProducer mqProducer;

    /**
     * 全员推送
     *
     * @param wsBaseVO
     * @param id
     */
    @Override
    public void sendPushMsg(WsBaseVO<?> wsBaseVO, Long id) {
        mqProducer.sendMsg(ChatMqConstant.CHAT_EXCHANGE, ChatMqConstant.WEBSOCKET_PUSH_ROUTING_KEY, wsBaseVO, id.toString());
    }
}
