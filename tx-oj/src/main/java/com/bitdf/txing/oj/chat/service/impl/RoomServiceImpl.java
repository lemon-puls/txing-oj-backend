package com.bitdf.txing.oj.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.enume.RoomStatusEnum;
import com.bitdf.txing.oj.chat.enume.RoomTypeEnum;
import com.bitdf.txing.oj.chat.mapper.RoomMapper;
import com.bitdf.txing.oj.chat.service.GroupMemberService;
import com.bitdf.txing.oj.chat.service.RoomFriendService;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.adapter.ChatAdapter;
import com.bitdf.txing.oj.chat.service.cache.RoomGroupCache;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserRelateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("roomService")
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    RoomFriendService roomFriendService;
    @Autowired
    RoomGroupCache roomGroupCache;
    @Autowired
    UserRelateCache userRelateCache;
    @Autowired
    GroupMemberService groupMemberService;
    @Autowired
    UserService userService;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<Room> page = this.page(
//                new Query<Room>().getPage(params),
//                new QueryWrapper<Room>()
//        );
//
//        return new PageUtils(page);
//    }


    /**
     * 创建聊天房间
     *
     * @param list
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createRoomAndRoomFriend(List<Long> list) {
        // 校验
        ThrowUtils.throwIf(list.isEmpty() || list.size() != 2, "房间创建失败，需要2个好友");
        // 查询是否已创建过房间
        List<Long> sortedList = ChatAdapter.sortUserIdList(list);
        RoomFriend roomFriend = roomFriendService.getByUserIds(sortedList.get(0), sortedList.get(1));
        if (roomFriend != null) {
            //  TODO 如果之前已经创建过 之前恢复之前的使用即可
            Room room = Room.builder()
                    .id(roomFriend.getRoomId())
                    .status(RoomStatusEnum.ACTIVE.getCode())
                    .build();
            boolean b = this.updateById(room);
        } else {
            Room room = createRoom(RoomTypeEnum.FRIEND);
            roomFriend = roomFriendService.createRoomFriend(room.getId(), sortedList.get(0), sortedList.get(1));
        }
        return roomFriend;
    }

    private Room createRoom(RoomTypeEnum friend) {
        Room insert = ChatAdapter.buildRoom(friend);
        this.save(insert);
        return insert;
    }

    /**
     * 更新房间的最新消息及时间
     */
    @Override
    public void refreshActiveMsgAndTime(Long roomId, Long msgId, Date createTime) {
        lambdaUpdate().eq(Room::getId, roomId)
                .set(Room::getMsgId, msgId)
                .set(Room::getActiveTime, createTime)
                .update();
    }

    @Override
    public Long disableRoomOfFriend(List<Long> sortUserIdList) {
        RoomFriend roomFriend = roomFriendService.lambdaQuery()
                .eq(RoomFriend::getUserId1, sortUserIdList.get(0))
                .eq(RoomFriend::getUserId2, sortUserIdList.get(1))
                .select(RoomFriend::getRoomId)
                .one();
        disableRoom(roomFriend.getRoomId());
        return roomFriend.getRoomId();
    }

    @Override
    public void disableRoom(Long roomId) {
        lambdaUpdate()
                .eq(Room::getId, roomId)
                .set(Room::getStatus, RoomStatusEnum.DISSOLVE.getCode())
                .update();
    }
}
