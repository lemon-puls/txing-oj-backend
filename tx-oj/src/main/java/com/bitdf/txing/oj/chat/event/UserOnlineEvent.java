package com.bitdf.txing.oj.chat.event;

import com.bitdf.txing.oj.model.entity.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Lizhiwei
 * @date 2024/1/7 21:51:54
 * 注释：
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private final User user;


    public UserOnlineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
