package com.bitdf.txing.oj.chat.service.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.MessagePageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.event.MessageSendEvent;
import com.bitdf.txing.oj.chat.service.ContactService;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.chat.service.adapter.MessageAdapter;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.chat.service.strategy.AbstractMsghandler;
import com.bitdf.txing.oj.chat.service.strategy.MsgHandlerFactory;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:13:56
 * 注释：
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    MessageService messageService;
    @Autowired
    ContactService contactService;

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

    /**
     * 获取消息响应体
     * @param msgId
     * @param userId
     * @return
     */
    @Override
    public ChatMessageVO getMessageVO(Long msgId, Long userId) {
        Message message = messageService.getById(msgId);
        return getMessageVO(message, userId);
    }

    private ChatMessageVO getMessageVO(Message message, Long userId) {
        return CollectionUtil.getFirst(getMessageVOBatch(Collections.singletonList(message), userId));
    }

    public List<ChatMessageVO> getMessageVOBatch(List<Message> messages, Long userId) {
        return MessageAdapter.buildMessageVOBatch(messages, userId);
    }

    /**
     * 消息-游标翻页
     * @param pageRequest
     * @param userId
     * @return
     */
    @Override
    public CursorPageBaseVO<ChatMessageVO> getMsgPageByCursor(MessagePageRequest pageRequest, Long userId) {
        Date lastMsgTime =  contactService.getUserContactLastMsgTime(pageRequest.getRoomId(), userId);
        CursorPageBaseVO<Message> cursorPageBaseVO = messageService.getPageByCursor(pageRequest.getRoomId(), pageRequest, lastMsgTime);
        if (cursorPageBaseVO.isEmpty()) {
            return CursorPageBaseVO.empty();
        }
        return CursorPageBaseVO.init(cursorPageBaseVO, MessageAdapter.buildMessageVOBatch(cursorPageBaseVO.getList(), userId));
    }
}