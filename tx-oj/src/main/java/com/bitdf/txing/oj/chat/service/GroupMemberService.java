package com.bitdf.txing.oj.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.chat.GroupMember;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
public interface GroupMemberService extends IService<GroupMember> {
    List<Long> getMemberListByGroupId(Long id);

//    PageUtils queryPage(Map<String, Object> params);
}

