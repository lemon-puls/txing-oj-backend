package com.bitdf.txing.oj.chat.service.adapter;

import com.bitdf.txing.oj.chat.enume.RoomStatusEnum;
import com.bitdf.txing.oj.chat.enume.RoomTypeEnum;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupAddRequest;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;
import com.bitdf.txing.oj.model.entity.chat.RoomGroup;
import com.bitdf.txing.oj.model.entity.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
        room.setStatus(RoomStatusEnum.ACTIVE.getCode());
        return room;
    }

    public static RoomFriend buildRoomFriend(Long roomId, Long userId1, Long userId2) {
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUserId1(userId1);
        roomFriend.setUserId2(userId2);
        roomFriend.setStatus(RoomStatusEnum.ACTIVE.getCode());
        return roomFriend;
    }

    /**
     * @param values
     * @param userId
     * @return
     */
    public static Set<Long> getFriendIdSet(Collection<RoomFriend> values, Long userId) {
        Set<Long> collect = values.stream().map((roomFriend) -> {
            return getFriendId(roomFriend, userId);
        }).collect(Collectors.toSet());
        return collect;
    }

    public static Long getFriendId(RoomFriend roomFriend, Long userId) {
        return roomFriend.getUserId1().equals(userId)
                ? roomFriend.getUserId2() : roomFriend.getUserId1();
    }

    public static RoomGroup buildRoomGroup(User user, Long roomId, GroupAddRequest groupAddRequest) {
        return RoomGroup.builder()
                .roomId(roomId)
                .name(groupAddRequest.getName())
                .avatar(Objects.nonNull(groupAddRequest.getGroupAvatar()) ? groupAddRequest.getGroupAvatar() : user.getUserAvatar())
                .build();
    }
}
