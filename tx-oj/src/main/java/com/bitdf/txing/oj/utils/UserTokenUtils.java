package com.bitdf.txing.oj.utils;

import com.bitdf.txing.oj.constant.RedisKeyConstant;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Lizhiwei
 * @date 2024/1/19 20:15:13
 * 注释：
 */
public class UserTokenUtils {

    /**
     * token有效期 默认30分钟
     */
    public static final int TOKEN_TIME_OUT = 300;

    /**
     * 生成 保存token
     *
     * @param userId
     * @return
     */
    public static String generateAndSaveUserToken(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN, userId);
        String token = RedisUtils.get(key);
        if (token != null) {
            RedisUtils.expire(key, TOKEN_TIME_OUT, TimeUnit.MINUTES);
            return token;
        } else {
            token = generateToken();
            RedisUtils.set(key, token, TOKEN_TIME_OUT, TimeUnit.MINUTES);
            return token;
        }
    }

    /**
     * 生成Token值
     *
     * @return
     */
    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    /**
     * 重置token过期时间
     * @param userId
     * @return
     */
    public static void resetTokenExpire(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN, userId);
        RedisUtils.expire(key, TOKEN_TIME_OUT, TimeUnit.MINUTES);
    }

    /**
     * 用户是否已登录
     */
    public static Boolean isLogin(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN, userId);
        return RedisUtils.get(key) != null;
    }
}
