package com.bitdf.txing.oj.chat.service.business;

import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.MessagePageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:13:33
 * 注释：
 */
public interface ChatService {
    Long sendMsg(ChatMessageRequest buildAgreeMessage, Long userId);

    ChatMessageVO getMessageVO(Long msgId, Long userId);

    CursorPageBaseVO<ChatMessageVO> getMsgPageByCursor(MessagePageRequest pageRequest, Long userId);
}
