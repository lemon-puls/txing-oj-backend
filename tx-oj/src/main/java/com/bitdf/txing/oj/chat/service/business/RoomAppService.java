package com.bitdf.txing.oj.chat.service.business;

import com.bitdf.txing.oj.chat.domain.vo.request.GroupAddRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupMemberRemoveRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupMemberRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMemberVO;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatRoomVO;
import com.bitdf.txing.oj.chat.domain.vo.response.GroupDetailVO;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/31 14:50:25
 * 注释：
 */
public interface RoomAppService {
    CursorPageBaseVO<ChatRoomVO> getContactPageByCursor(CursorPageBaseRequest cursorPageBaseRequest, Long userId);

    List<ChatRoomVO> buildContactResp(Long userId, List<Long> roomIds);

    ChatRoomVO getContactDetailByRoomId(Long roomId, Long userId);

    ChatRoomVO getContactDetailByFriendId(Long userId, Long friendId);

    GroupDetailVO getGroupDetail(Long userId, Long roomId);

    CursorPageBaseVO<ChatMemberVO> getGroupMembersByCursor(GroupMemberRequest groupMemberRequest);

    Long addGroup(GroupAddRequest groupAddRequest, Long userId);

    void deleteFriendRoom(List<Long> asList);

    void disableRoom(List<Long> asList);

    void removeGroupMember(GroupMemberRemoveRequest groupMemberRemoveRequest);
}
