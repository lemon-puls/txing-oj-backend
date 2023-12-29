package com.bitdf.txing.oj.chat.service.adapter;

import com.bitdf.txing.oj.chat.domain.enume.RoomFriendStatusEnum;
import com.bitdf.txing.oj.chat.domain.enume.RoomTypeEnum;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/29 14:12:33
 * 注释：
 */
public class ChatAdapter {

    public static final String SEPARATOR = "-";

    public static String generateRoomKey(List<Long> userIds) {
        return userIds.stream().sorted().map(String::valueOf).collect(Collectors.joining(SEPARATOR));
    }

    /**
     * 对用户id进行排序
     *
     * @param userIds
     * @return
     */
    public static List<Long> sortUserIdList(List<Long> userIds) {
        return userIds.stream().sorted().collect(Collectors.toList());
    }

    public static Room buildRoom(RoomTypeEnum typeEnum) {
        Room room = new Room();
        room.setType(typeEnum.getCode());
        room.setHotFlag(false);
        return room;
    }

    public static RoomFriend buildRoomFriend(Long roomId, Long userId1, Long userId2) {
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUserId1(userId1);
        roomFriend.setUserId2(userId2);
        roomFriend.setStatus(RoomFriendStatusEnum.ACTIVE.getCode());
        return roomFriend;
    }
}
