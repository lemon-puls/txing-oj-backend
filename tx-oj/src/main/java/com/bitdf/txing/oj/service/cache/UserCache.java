package com.bitdf.txing.oj.service.cache;

import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/31 22:07:16
 * 注释：
 */
@Component
public class UserCache extends AbstractRedisStringCache<Long, User> {

    @Autowired
    UserService userService;

    @Override
    protected String getKey(Long userId) {
        return RedisKeyConstant.getKey(RedisKeyConstant.USER_STRING, userId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, User> load(List<Long> userIdList) {
        List<User> users = userService.listByIds(userIdList);
        return users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
