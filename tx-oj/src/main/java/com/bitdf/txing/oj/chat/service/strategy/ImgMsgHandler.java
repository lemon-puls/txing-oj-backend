package com.bitdf.txing.oj.chat.service.strategy;

import com.bitdf.txing.oj.chat.domain.vo.request.ImgMessageRequest;
import com.bitdf.txing.oj.chat.enume.MessageTypeEnum;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.entity.chat.msg.MessageExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Lizhiwei
 * @date 2024/1/24 15:04:36
 * 注释：
 */
@Component
public class ImgMsgHandler extends AbstractMsghandler<ImgMessageRequest> {
    @Autowired
    MessageService messageService;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    public void saveMsg(Message message, ImgMessageRequest body) {
        MessageExtra messageExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(messageExtra);
        messageExtra.setImgMesssageRequest(body);
        messageService.updateById(update);
    }

    @Override
    public Object showMsg(Message message) {
        return message.getExtra().getImgMesssageRequest();
    }

    @Override
    public String showContactMsg(Message message) {
        return "[图片]";
    }
}
