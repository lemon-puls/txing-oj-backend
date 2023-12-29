package com.bitdf.txing.oj.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Lizhiwei
 * @date 2023/12/29 21:34:40
 * 注释：
 */
@Getter
public class MessageSendEvent extends ApplicationEvent {

    private Long msgId;

    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
