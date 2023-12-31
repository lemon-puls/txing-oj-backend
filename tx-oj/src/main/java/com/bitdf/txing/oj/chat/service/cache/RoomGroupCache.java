package com.bitdf.txing.oj.chat.service.cache;

import com.bitdf.txing.oj.chat.service.RoomGroupService;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.model.entity.chat.RoomGroup;
import com.bitdf.txing.oj.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/31 20:09:57
 * 注释：
 */
@Component
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {

    @Autowired
    RoomGroupService roomGroupService;

    @Override
    protected String getKey(Long roomId) {
        return RedisKeyConstant.getKey(RedisKeyConstant.GROUP_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> roomIds) {
        List<RoomGroup> roomGroups = roomGroupService.listByRoomIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomGroup::getRoomId, Function.identity()));
    }
}
