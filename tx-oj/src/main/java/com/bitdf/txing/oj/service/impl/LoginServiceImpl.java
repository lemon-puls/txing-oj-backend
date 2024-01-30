package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.util.StrUtil;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.service.LoginService;
import com.bitdf.txing.oj.utils.JwtUtils;
import com.bitdf.txing.oj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lizhiwei
 * @date 2024/1/30 20:30:59
 * 注释：
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {


    public static final Integer TOKEN_EXPIRE = 5;
    public static final Integer TOKEN_RENEW = 2;

    @Override
    public boolean verifyToken(String token) {
        Long userId = JwtUtils.getUserIdOrNull(token);
        if (Objects.isNull(userId)) {
            return false;
        }
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN, userId);
        String realToken = RedisUtils.get(key);
        return Objects.equals(realToken, token);
    }

    @Async
    @Override
    public void renewTokenIfNecessary(String token) {
        Long userId = JwtUtils.getUserIdOrNull(token);
        if (Objects.isNull(userId)) {
            return;
        }
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN, userId);
        Long expire = RedisUtils.getExpire(key, TimeUnit.DAYS);
        if (expire == -2) {
            return;
        }
        if (expire < TOKEN_RENEW) {
            RedisUtils.expire(key, TOKEN_EXPIRE, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN, userId);
        String token = RedisUtils.get(key);
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        String token1 = JwtUtils.createToken(userId);
        RedisUtils.set(key, token1, TOKEN_EXPIRE, TimeUnit.DAYS);
        return token1;
    }

    @Override
    public Long getValidUserId(String token) {
        boolean b = verifyToken(token);
        return b ? JwtUtils.getUserIdOrNull(token) : null;
    }

    /**
     * 退出登录
     * @param userId
     * @return
     */
    @Override
    public boolean userLogout(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN, userId);
        RedisUtils.del(key);
        return true;
    }
}
