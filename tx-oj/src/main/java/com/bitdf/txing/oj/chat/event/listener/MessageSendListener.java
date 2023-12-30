package com.bitdf.txing.oj.chat.event.listener;

import com.bitdf.txing.oj.chat.constant.ChatMqConstant;
import com.bitdf.txing.oj.chat.event.MessageSendEvent;
import com.bitdf.txing.oj.utils.MqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author Lizhiwei
 * @date 2023/12/29 21:44:15
 * 注释：
 */
@Slf4j
@Component
public class MessageSendListener {

    @Autowired
    MqProducer mqProducer;


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        log.info("[处理消息发送事件]：msgId: {}", msgId);
        mqProducer.sendMsg(ChatMqConstant.CHAT_EXCHANGE, ChatMqConstant.MESSAGE_SEND_ROUTING_KEY, msgId, msgId.toString());
    }
}
