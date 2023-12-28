package com.bitdf.txing.oj.chat.service.impl;

import com.bitdf.txing.oj.chat.mapper.GroupMapper;
import com.bitdf.txing.oj.model.entity.chat.Group;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.service.GroupService;


@Service("groupService")
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

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
