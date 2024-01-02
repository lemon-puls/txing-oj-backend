package com.bitdf.txing.oj.service.cache;

import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.utils.RedisUtils;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2024/1/1 13:24:14
 * 注释：
 */
@Component
public class UserRelateCache {

    /**
     * 获取在线用户数
     * @return
     */
    public Long getOnlineCount() {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.ONLINE_USERID_ZET);
        return RedisUtils.zCard(key);
    }
}
