package com.bitdf.txing.oj.event;

import com.bitdf.txing.oj.model.entity.user.UserApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author Lizhiwei
 * @date 2023/12/29 10:17:08
 * 注释：
 */
@Getter
public class UserApplyEvent extends ApplicationEvent {

    private UserApply userApply;

    public UserApplyEvent(Object source, UserApply userApply) {
        super(source);
        this.userApply = userApply;
    }
}
