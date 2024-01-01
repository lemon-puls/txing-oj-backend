package com.bitdf.txing.oj.chat.service.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.chat.domain.enume.MessageTypeEnum;
import com.bitdf.txing.oj.chat.domain.vo.request.TextMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.TextMessageResponse;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.entity.chat.msg.MessageExtra;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Lizhiwei
 * @date 2023/12/29 19:16:01
 * 注释： 文本消息处理器
 */
@Component
public class TextMsgHandler extends AbstractMsghandler<TextMessageRequest> {
    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    @Override
    protected void checkMsg(TextMessageRequest body, Long roomId, Long userId) {
        // TODO 校验消息
        super.checkMsg(body, roomId, userId);
    }

    @Override
    protected void saveMsg(Message message, TextMessageRequest body) {
        // 消息扩展信息
        MessageExtra messageExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setContent(body.getContent());
        update.setExtra(messageExtra);
        update.setId(message.getId());
        // 艾特功能
        if (CollectionUtil.isNotEmpty(body.getAtUserIds())) {
            messageExtra.setAtUserIds(body.getAtUserIds());
        }
        this.messageService.updateById(update);
    }

    /**
     * 把消息转换为方便前端展示的形式
     *
     * @param message
     * @return
     */
    @Override
    public Object showMsg(Message message) {
        TextMessageResponse response = new TextMessageResponse();
        response.setContent(message.getContent());
        response.setAtUserIds(Optional.ofNullable(message.getExtra()).map(MessageExtra::getAtUserIds).orElse(null));
        return response;
    }

    @Override
    public String showContactMsg(Message message) {
        return message.getContent();
    }
}
