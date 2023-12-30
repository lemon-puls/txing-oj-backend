package com.bitdf.txing.oj.chat.service.impl;

import com.bitdf.txing.oj.chat.mapper.RoomGroupMapper;
import com.bitdf.txing.oj.model.entity.chat.RoomGroup;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.service.RoomGroupService;


@Service("groupService")
public class RoomGroupServiceImpl extends ServiceImpl<RoomGroupMapper, RoomGroup> implements RoomGroupService {

    @Override
    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery().eq(RoomGroup::getRoomId, roomId).one();
    }

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<GroupEntity> page = this.page(
//                new Query<GroupEntity>().getPage(params),
//                new QueryWrapper<GroupEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}
