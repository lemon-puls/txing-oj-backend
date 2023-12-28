package com.bitdf.txing.oj.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.mapper.RoomMapper;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.model.entity.chat.Room;
import org.springframework.stereotype.Service;


@Service("roomService")
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<Room> page = this.page(
//                new Query<Room>().getPage(params),
//                new QueryWrapper<Room>()
//        );
//
//        return new PageUtils(page);
//    }

}
