package com.bitdf.txing.oj.chat.service.cache;

import com.bitdf.txing.oj.chat.service.GroupMemberService;
import com.bitdf.txing.oj.chat.service.RoomGroupService;
import com.bitdf.txing.oj.model.entity.chat.RoomGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author Lizhiwei
 * @date 2023/12/30 22:34:32
 * 注释：
 */
@Component
public class GroupMemberCache {

    @Autowired
    RoomGroupService roomGroupService;
    @Autowired
    GroupMemberService groupMemberService;

    @Cacheable(cacheNames = "oj:chat:group", key = "'groupMember'+#roomId")
    public List<Long> getMemberUserIdList(Long roomId) {
        RoomGroup roomGroup = roomGroupService.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return null;
        }
        return groupMemberService.getMemberListByGroupId(roomGroup.getId());
    }
}
