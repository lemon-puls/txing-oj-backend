package com.bitdf.txing.oj.chat.service.cache;

import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/29 21:11:25
 * 注释：
 */
@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {

    @Autowired
    RoomService roomService;

    /**
     * 获取key
     * @param roomId
     * @return
     */
    @Override
    protected String getKey(Long roomId) {
        return RedisKeyConstant.getKey(RedisKeyConstant.ROOM_INFO, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    /**
     *
     * @param roomIds
     * @return
     */
    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomService.listByIds(roomIds);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
