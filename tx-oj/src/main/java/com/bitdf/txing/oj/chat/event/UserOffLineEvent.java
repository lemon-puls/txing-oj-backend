package com.bitdf.txing.oj.chat.event;

import com.bitdf.txing.oj.model.entity.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Lizhiwei
 * @date 2024/1/2 22:04:23
 * 注释：
 */
@Getter
public class UserOffLineEvent extends ApplicationEvent {

    private final User user;


    public UserOffLineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
