package com.bitdf.txing.oj.chat.domain.dto;

import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.enume.WsPushTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/30 22:09:34
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushMsgMqDTO {
    /**
     * 要推送的ws消息
     */
    WsBaseVO<?> wsBaseVO;
    /**
     * 目标用户ID s
     */
    private List<Long> userIds;
    /**
     * 推送类型 0：部分 1：全员
     */
    private Integer type;

    public PushMsgMqDTO(WsBaseVO<?> wsBaseVO) {
        this.wsBaseVO = wsBaseVO;
        this.type = WsPushTypeEnum.ALL.getType();
    }

    public PushMsgMqDTO(WsBaseVO<?> wsBaseVO, List<Long> targetUserIds) {
        this.wsBaseVO = wsBaseVO;
        this.userIds = targetUserIds;
        this.type = WsPushTypeEnum.USER.getType();
    }
}
