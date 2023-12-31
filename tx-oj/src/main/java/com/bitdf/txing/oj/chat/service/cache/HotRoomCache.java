package com.bitdf.txing.oj.chat.service.cache;

import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:03:40
 * 注释：
 */
@Component
public class HotRoomCache {

    /**
     * 刷新活跃时间
     */
    public void refreshActiveTime(Long roomId, Date time) {
        RedisUtils.zAdd(RedisKeyConstant.HOT_ROOM_ZET, roomId, (double) time.getTime());
    }

    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotStart, Double hotEnd) {
        return RedisUtils.zRangeByScoreWithScores(RedisKeyConstant.getKey(RedisKeyConstant.HOT_ROOM_ZET), hotStart, hotEnd);
    }

}
