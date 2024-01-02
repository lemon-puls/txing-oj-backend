package com.bitdf.txing.oj.chat.enume;

import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsGroupMemberChangeVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:30:21
 * 注释：
 */
@Getter
@AllArgsConstructor
public enum WsRespTypeEnum {

    MESSAGE(0,"新消息",ChatMessageVO.class),
    GROUP_MEMBER_CHANGE(1, "群聊成员变动", WsGroupMemberChangeVO.class);

    private final Integer type;
    private final String desc;
    private final Class dataClass;
}
