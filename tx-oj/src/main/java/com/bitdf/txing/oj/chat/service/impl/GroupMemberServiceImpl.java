package com.bitdf.txing.oj.chat.service.impl;

import com.bitdf.txing.oj.chat.mapper.GroupMemberMapper;
import com.bitdf.txing.oj.chat.service.GroupMemberService;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.GroupMember;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("groupMemberService")
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {
    @Override
    public List<Long> getMemberListByGroupId(Long groupId) {
        List<GroupMember> list = lambdaQuery().eq(GroupMember::getGroupId, groupId)
                .select(GroupMember::getUserId)
                .list();
        return list.stream().map(GroupMember::getUserId).collect(Collectors.toList());
    }

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<GroupMemberEntity> page = this.page(
//                new Query<GroupMemberEntity>().getPage(params),
//                new QueryWrapper<GroupMemberEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public GroupMember getMember(Long userId, Long groupId) {
        return lambdaQuery().eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId)
                .one();
    }

    /**
     * 获取普通群聊的成员（游标分页）
     * @param request
     * @param groupId
     * @return
     */
    @Override
    public CursorPageBaseVO<GroupMember> getMembersPageByCursor(CursorPageBaseRequest request, Long groupId) {
        CursorPageBaseVO<GroupMember> cursorPageBaseVO = CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(GroupMember::getGroupId, groupId);
        }, GroupMember::getCreateTime);
        return cursorPageBaseVO;
    }
}
