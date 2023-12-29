package com.bitdf.txing.oj.chat.service.adapter;

import com.bitdf.txing.oj.chat.domain.enume.MessageTypeEnum;
import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.TextMessageRequest;
import com.bitdf.txing.oj.model.entity.chat.Message;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:25:09
 * 注释：
 */
public class MessageAdapter {

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
}
