package com.bitdf.txing.oj.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
public interface RoomService extends IService<Room> {
    RoomFriend createRoomAndRoomFriend(List<Long> asList);

    void refreshActiveMsgAndTime(Long id, Long id1, Date createTime);

    Long disableRoomOfFriend(List<Long> sortUserIdList);

    void disableRoom(Long roomId);


//    PageUtils queryPage(Map<String, Object> params);
}

