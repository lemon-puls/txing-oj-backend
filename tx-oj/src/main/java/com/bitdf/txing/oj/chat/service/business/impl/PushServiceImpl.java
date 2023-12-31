package com.bitdf.txing.oj.chat.service.business.impl;

import com.bitdf.txing.oj.chat.constant.ChatMqConstant;
import com.bitdf.txing.oj.chat.domain.dto.PushMsgMqDTO;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.utils.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        mqProducer.sendMsg(ChatMqConstant.CHAT_EXCHANGE, ChatMqConstant.WEBSOCKET_PUSH_ROUTING_KEY, new PushMsgMqDTO(wsBaseVO), id.toString());
    }

    /**
     * 推送指定用户
     * @param wsBaseVO
     * @param targetUserIds
     * @param id
     */
    @Override
    public void sendPushMsg(WsBaseVO<ChatMessageVO> wsBaseVO, List<Long> targetUserIds, Long id) {
        mqProducer.sendMsg(ChatMqConstant.CHAT_EXCHANGE, ChatMqConstant.WEBSOCKET_PUSH_ROUTING_KEY,
                new PushMsgMqDTO(wsBaseVO, targetUserIds), id.toString());
    }
}
