package com.bitdf.txing.oj.chat.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2024/1/2 22:57:05
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsOnlineOfflineNotifyVO {
    /**
     * 上线/下线的用户
     */
    private List<ChatMemberVO> chatMemberVOS = new ArrayList<>();
    /**
     * 当前在线人数
     */
    private Long onlineNum;
}
