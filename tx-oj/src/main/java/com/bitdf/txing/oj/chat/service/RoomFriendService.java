package com.bitdf.txing.oj.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;

import java.util.List;

/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
public interface RoomFriendService extends IService<RoomFriend> {
    RoomFriend getByUserIds(Long aLong, Long aLong1);

    RoomFriend createRoomFriend(Long roomId, Long aLong, Long aLong1);

    RoomFriend getByRoomId(Long id);

    List<RoomFriend> listByRoomIds(List<Long> roomIds);

//    PageUtils queryPage(Map<String, Object> params);
}

