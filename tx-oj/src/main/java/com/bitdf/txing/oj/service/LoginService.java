package com.bitdf.txing.oj.service;

import org.springframework.scheduling.annotation.Async;

/**
 * @author Lizhiwei
 * @date 2024/1/30 20:30:36
 * 注释：
 */
public interface LoginService {
    boolean verifyToken(String token);

    @Async
    void renewTokenIfNecessary(String token);

    String login(Long userId);

    Long getValidUserId(String token);

    boolean userLogout(Long userId);
}
