package com.bitdf.txing.oj.chat.service.business;

import com.bitdf.txing.oj.chat.domain.vo.response.ChatRoomVO;
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
}
