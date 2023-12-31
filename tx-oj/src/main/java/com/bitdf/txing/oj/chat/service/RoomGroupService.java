package com.bitdf.txing.oj.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.chat.RoomGroup;

import java.util.List;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
public interface RoomGroupService extends IService<RoomGroup> {
    RoomGroup getByRoomId(Long roomId);

    List<RoomGroup> listByRoomIds(List<Long> roomIds);

//    PageUtils queryPage(Map<String, Object> params);
}

