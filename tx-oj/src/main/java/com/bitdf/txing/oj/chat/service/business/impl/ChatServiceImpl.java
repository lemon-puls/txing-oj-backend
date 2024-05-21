package com.bitdf.txing.oj.chat.service.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.MessagePageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMemberStatisticVO;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.enume.RoomStatusEnum;
import com.bitdf.txing.oj.chat.enume.RoomTypeEnum;
import com.bitdf.txing.oj.chat.event.MessageSendEvent;
import com.bitdf.txing.oj.chat.service.ContactService;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.adapter.MessageAdapter;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.chat.service.cache.GroupMemberCache;
import com.bitdf.txing.oj.chat.service.strategy.AbstractMsghandler;
import com.bitdf.txing.oj.chat.service.strategy.MsgHandlerFactory;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
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
    @Autowired
    MessageAdapter messageAdapter;
    @Autowired
    RoomService roomService;
    @Autowired
    GroupMemberCache groupMemberCache;

    /**
     * 发送消息
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageRequest chatMessageRequest, Long userId) {
        // 检查Room是否可用
        Room room = roomService.getById(chatMessageRequest.getRoomId());
        if (RoomStatusEnum.DISSOLVE.getCode().equals(room.getStatus())) {
            if (RoomTypeEnum.FRIEND.getCode().equals(room.getType())) {
                throw new BusinessException(TxCodeEnume.COMMON_CUSTOM_EXCEPTION, "你们不是好友关系 无法发送消息");
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_CUSTOM_EXCEPTION, "该群聊已解散 无法发送消息");
            }
        }
        if (RoomStatusEnum.FORBIDDEN.getCode().equals(room.getStatus())) {
            // 房间已被封禁
            throw new BusinessException(TxCodeEnume.COMMON_CUSTOM_EXCEPTION, "该房间已被封禁 无法发送消息");
        }
        // 如果是群聊 检查当前用户是否是群聊成员
        if (RoomTypeEnum.GROUP.getCode().equals(room.getType()) && !Room.HOT_ROOM_ID.equals(room.getId())) {
            List<Long> memberUserIdList = groupMemberCache.getMemberUserIdList(chatMessageRequest.getRoomId());
            if (!memberUserIdList.contains(userId) && !User.SYSTEM_USER_ID.equals(userId)) {
                throw new BusinessException(TxCodeEnume.COMMON_CUSTOM_EXCEPTION, "你当前不是该群聊成员 无法发送消息");
            }
        }
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
        return messageAdapter.buildMessageVOBatch(messages, userId);
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
        Date lastMsgTime;
        if (Room.HOT_ROOM_ID.equals(pageRequest.getRoomId())) {
            lastMsgTime = new Date();
        } else {
            lastMsgTime = contactService.getUserContactLastMsgTime(pageRequest.getRoomId(), userId);
        }
        CursorPageBaseVO<Message> cursorPageBaseVO = messageService.getPageByCursor(pageRequest.getRoomId(), pageRequest, lastMsgTime);
        if (cursorPageBaseVO.isEmpty()) {
            return CursorPageBaseVO.empty();
        }
        return CursorPageBaseVO.init(cursorPageBaseVO, messageAdapter.buildMessageVOBatch(cursorPageBaseVO.getList(), userId));
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
