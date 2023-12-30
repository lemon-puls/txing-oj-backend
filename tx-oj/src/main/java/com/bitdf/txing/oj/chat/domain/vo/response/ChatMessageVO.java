package com.bitdf.txing.oj.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/30 15:00:28
 * 注释：
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageVO {
    @ApiModelProperty("发送方消息")
    private UserInfo fromUser;
    @ApiModelProperty("消息详情")
    private Message message;


    @Data
    @Builder
    public static class UserInfo {
        @ApiModelProperty("用户id")
        private Long userId;
    }

    @Data
    public static class Message {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("聊天房间id")
        private Long roomId;
        @ApiModelProperty("消息发送时间")
        private Date sendTime;
        @ApiModelProperty("消息类型 0：文本消息")
        private Integer type;
        @ApiModelProperty("消息内容")
        private Object body;
    }
}
