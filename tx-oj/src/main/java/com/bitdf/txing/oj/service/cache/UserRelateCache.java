package com.bitdf.txing.oj.service.cache;

import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2024/1/1 13:24:14
 * 注释：
 */
@Component
public class UserRelateCache {

    /**
     * 获取在线用户数
     *
     * @return
     */
    public Long getOnlineCount() {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.ONLINE_USERID_ZET);
        return RedisUtils.zCard(key);
    }

    /**
     * @param userId
     * @param lastOpsTime
     */
    public void offLine(Long userId, Date lastOpsTime) {
        String onlineKey = RedisKeyConstant.getKey(RedisKeyConstant.ONLINE_USERID_ZET);
        String offlineKey = RedisKeyConstant.getKey(RedisKeyConstant.OFFLINE_USERID_ZET);
        // 更新Redis中上线用户集合
        RedisUtils.zRemove(onlineKey, userId);
        // 更新Redis中下线用户集合
        RedisUtils.zAdd(offlineKey, userId, lastOpsTime.getTime());
    }


    public boolean isOnline(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.ONLINE_USERID_ZET);
        return RedisUtils.zIsMember(key, userId);
    }

    public void online(Long userId, Date lastOpsTime) {
        String onlineKey = RedisKeyConstant.getKey(RedisKeyConstant.ONLINE_USERID_ZET);
        String offlineKey = RedisKeyConstant.getKey(RedisKeyConstant.OFFLINE_USERID_ZET);
        RedisUtils.zRemove(offlineKey, userId);
        RedisUtils.zAdd(onlineKey, userId, lastOpsTime.getTime());
    }

    // 刷新用户信息修改时间
    public void refreshUserModifyTime(Long userId) {
        String key = RedisKeyConstant.getKey(RedisKeyConstant.USER_MODIFY_TIME, userId);
        RedisUtils.set(key, new Date().getTime());
    }
    // 批量获取修改时间
    public List<Long> getUserModifyTimeBatch(List<Long> userList) {
        List<String> keys = userList.stream().map(userId -> RedisKeyConstant.getKey(RedisKeyConstant.USER_MODIFY_TIME, userId))
                .collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }
}
