package com.bitdf.txing.oj.chat.service.strategy;

import cn.hutool.core.bean.BeanUtil;
import com.bitdf.txing.oj.chat.domain.enume.MessageTypeEnum;
import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.chat.service.adapter.MessageAdapter;
import com.bitdf.txing.oj.model.entity.chat.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * @author Lizhiwei
 * @date 2023/12/29 18:17:39
 * 注释：
 */
public abstract class AbstractMsghandler<request> {


    private Class<request> bodyClass;

    @Autowired
    MessageService messageService;

    @PostConstruct
    public void init() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<request>) genericSuperclass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getCode(), this);
    }

    abstract MessageTypeEnum getMsgTypeEnum();

    /**
     * 校验 保存消息
     *
     * @param messageRequest
     * @param userId
     * @return
     */
    @Transactional
    public Long checkAndSaveMsg(ChatMessageRequest messageRequest, Long userId) {
        request body = this.toBean(messageRequest.getBody());
        // TODO 统一校验
        // 子类实现校验
        checkMsg(body, messageRequest.getRoomId(), userId);
        // 统一保存
        Message message = MessageAdapter.buildMessageSave(messageRequest, userId);
        messageService.save(message);
        // 子类保存body
        saveMsg(message, body);
        return message.getId();
    }

    protected abstract void saveMsg(Message message, request body);

    protected void checkMsg(request body, Long roomId, Long userId) {

    }

    /**
     * 将消息体body装换为对应的消息类型
     */
    public request toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) {
            return (request) body;
        }
        return BeanUtil.toBean(body, bodyClass);
    }

    /**
     * 把消息转换为方便前端展示的形式
     * @param message
     * @return
     */
    public abstract Object showMsg(Message message);
}
