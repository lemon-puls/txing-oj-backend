package com.bitdf.txing.oj.chat.service.strategy;

import com.bitdf.txing.oj.chat.domain.enume.MessageTypeEnum;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.model.entity.chat.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2024/1/2 12:52:14
 * 注释：
 */
@Component
public class SystemMsgHandler extends AbstractMsghandler<String> {
    @Autowired
    MessageService messageService;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SYSTEM;
    }

    @Override
    protected void saveMsg(Message message, String body) {
        Message update = new Message();
        update.setId(message.getId());
        update.setContent(body);
        messageService.updateById(update);
    }

    @Override
    public Object showMsg(Message message) {
        return message.getContent();
    }

    @Override
    public String showContactMsg(Message message) {
        return message.getContent();
    }
}
