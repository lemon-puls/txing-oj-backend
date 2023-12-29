package com.bitdf.txing.oj.chat.service.business.impl;

import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.chat.service.strategy.AbstractMsghandler;
import com.bitdf.txing.oj.chat.service.strategy.MsgHandlerFactory;
import com.bitdf.txing.oj.event.MessageSendEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:13:56
 * 注释：
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发送消息
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageRequest chatMessageRequest, Long userId) {
        // TODO 检查合法性
        // 保存消息
        AbstractMsghandler msghandler = MsgHandlerFactory.getStrategyNoNull(chatMessageRequest.getMsgType());
        Long msgId = msghandler.checkAndSaveMsg(chatMessageRequest, userId);
        // 触发消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        return msgId;
    }
}
