package com.bitdf.txing.oj.chat.service.cache;

import com.bitdf.txing.oj.chat.service.RoomFriendService;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;
import com.bitdf.txing.oj.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/31 19:28:52
 * 注释：
 */
@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {

    @Autowired
    private RoomFriendService roomFriendService;

    @Override
    protected String getKey(Long roomId) {
        return RedisKeyConstant.getKey(RedisKeyConstant.GROUP_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomFriends = roomFriendService.listByRoomIds(roomIds);
        return roomFriends.stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
    }
}
