package com.bitdf.txing.oj.chat.service.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.MessagePageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMemberStatisticVO;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.event.MessageSendEvent;
import com.bitdf.txing.oj.chat.service.ContactService;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.chat.service.adapter.MessageAdapter;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.chat.service.strategy.AbstractMsghandler;
import com.bitdf.txing.oj.chat.service.strategy.MsgHandlerFactory;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.service.cache.UserRelateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    UserRelateCache userRelateCache;

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
     *
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
     *
     * @param pageRequest
     * @param userId
     * @return
     */
    @Override
    public CursorPageBaseVO<ChatMessageVO> getMsgPageByCursor(MessagePageRequest pageRequest, Long userId) {
        Date lastMsgTime = contactService.getUserContactLastMsgTime(pageRequest.getRoomId(), userId);
        CursorPageBaseVO<Message> cursorPageBaseVO = messageService.getPageByCursor(pageRequest.getRoomId(), pageRequest, lastMsgTime);
        if (cursorPageBaseVO.isEmpty()) {
            return CursorPageBaseVO.empty();
        }
        return CursorPageBaseVO.init(cursorPageBaseVO, MessageAdapter.buildMessageVOBatch(cursorPageBaseVO.getList(), userId));
    }

    /**
     * 消息阅读上报
     *
     * @param userId
     * @param roomId
     */
    @Override
    // TODO 上锁
    public void msgRead(Long userId, Long roomId) {
        Contact contact = contactService.getByUserIdAndRoomId(userId, roomId);
        if (Objects.nonNull(contact)) {
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactService.updateById(update);
        } else {
            Contact insert = new Contact();
            insert.setRoomId(roomId);
            insert.setUserId(userId);
            insert.setReadTime(new Date());
            contactService.save(insert);
        }
    }

    /**
     * 获取成员相关的一些统计信息
     *
     * @return
     */
    @Override
    public ChatMemberStatisticVO getChatMemberStatisticVO() {
        Long onlineCount = userRelateCache.getOnlineCount();
        return ChatMemberStatisticVO.builder()
                .onlineNum(onlineCount)
                .build();
    }
}
