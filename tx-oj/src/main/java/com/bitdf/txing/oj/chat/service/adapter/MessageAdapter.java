package com.bitdf.txing.oj.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.bitdf.txing.oj.chat.enume.MessageTypeEnum;
import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.TextMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.service.strategy.AbstractMsghandler;
import com.bitdf.txing.oj.chat.service.strategy.MsgHandlerFactory;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.entity.chat.RoomGroup;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:25:09
 * 注释：
 */
@Component
public class MessageAdapter {

    @Autowired
    UserCache userCache;

    /**
     * 构建同意好友请求消息
     *
     * @param roomId
     * @return
     */
    public static ChatMessageRequest buildAgreeMessage(Long roomId) {
        ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setRoomId(roomId);
        chatMessageRequest.setMsgType(MessageTypeEnum.TEXT.getCode());
        TextMessageRequest textMessageRequest = new TextMessageRequest();
        textMessageRequest.setContent("我们已经是好友啦，开始聊天吧！");
        chatMessageRequest.setBody(textMessageRequest);
        return chatMessageRequest;
    }

    public static Message buildMessageSave(ChatMessageRequest messageRequest, Long userId) {
        return Message.builder().fromUserId(userId)
                .roomId(messageRequest.getRoomId())
                .type(messageRequest.getMsgType())
                .build();
    }

    public List<ChatMessageVO> buildMessageVOBatch(List<Message> messages, Long userId) {
        List<ChatMessageVO> collect = messages.stream().map(message -> {
                    ChatMessageVO chatMessageVO = new ChatMessageVO();
                    chatMessageVO.setFromUser(buildFromUser(message.getFromUserId()));
                    chatMessageVO.setMessage(buildMessage(message));
                    return chatMessageVO;
                })
                .sorted(Comparator.comparing(item -> item.getMessage().getSendTime()))
                .collect(Collectors.toList());
        return collect;
    }

    private static ChatMessageVO.Message buildMessage(Message message) {
        ChatMessageVO.Message messageVO = new ChatMessageVO.Message();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());
        AbstractMsghandler msgHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        messageVO.setBody(msgHandler.showMsg(message));
        return messageVO;
    }

    public ChatMessageVO.UserInfo buildFromUser(Long fromUserId) {
        Map<Long, User> batch = userCache.getBatch(Arrays.asList(fromUserId));
        User user = batch.get(fromUserId);
        return ChatMessageVO.UserInfo
                .builder()
                .userId(fromUserId)
                .userName(user.getUserName())
                .userAvatar(user.getUserAvatar())
                .build();
    }

    /**
     * 构建 群聊成员添加的通知消息
     *
     * @param roomGroup
     * @param inviteUser
     * @param newMemberMap
     * @return
     */
    public static ChatMessageRequest buildGroupMemberAddMessage(RoomGroup roomGroup, User inviteUser, Map<Long, User> newMemberMap) {
        ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setRoomId(chatMessageRequest.getRoomId());
        chatMessageRequest.setMsgType(MessageTypeEnum.SYSTEM.getCode());
        StringBuffer sb = new StringBuffer();
        sb.append("\"")
                .append(inviteUser.getUserName())
                .append("\"")
                .append("邀请")
                .append(newMemberMap.values().stream().map(user -> {
                    return "\"" + user.getUserName() + "\"";
                }).collect(Collectors.joining(",")))
                .append("加入群聊");
        chatMessageRequest.setBody(sb.toString());
        return chatMessageRequest;
    }
}
