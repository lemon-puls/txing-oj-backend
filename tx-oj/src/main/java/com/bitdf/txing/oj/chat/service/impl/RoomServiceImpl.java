package com.bitdf.txing.oj.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.domain.enume.RoomTypeEnum;
import com.bitdf.txing.oj.chat.mapper.RoomMapper;
import com.bitdf.txing.oj.chat.service.RoomFriendService;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.adapter.ChatAdapter;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("roomService")
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    RoomFriendService roomFriendService;

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
}
