package com.bitdf.txing.oj.chat.service.business;

import com.bitdf.txing.oj.chat.domain.vo.response.ChatRoomVO;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;

/**
 * @author Lizhiwei
 * @date 2023/12/31 14:50:25
 * 注释：
 */
public interface RoomAppService {
    CursorPageBaseVO<ChatRoomVO> getContactPageByCursor(CursorPageBaseRequest cursorPageBaseRequest, Long userId);
}
