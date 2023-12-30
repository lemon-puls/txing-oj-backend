package com.bitdf.txing.oj.chat.service.business;

import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:13:33
 * 注释：
 */
public interface ChatService {
    Long sendMsg(ChatMessageRequest buildAgreeMessage, Long userId);

    ChatMessageVO getMessageVO(Long msgId, Long userId);
}
