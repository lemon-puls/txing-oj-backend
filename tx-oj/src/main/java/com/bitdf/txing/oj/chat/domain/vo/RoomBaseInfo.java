package com.bitdf.txing.oj.chat.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/31 18:46:01
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomBaseInfo {
    @ApiModelProperty("房间id")
    private Long roomId;
    @ApiModelProperty("会话名称")
    private String name;
    @ApiModelProperty("会话头像")
    private String avatar;
    @ApiModelProperty("房间类型 0：私聊 1 ：群聊")
    private Integer type;
    @ApiModelProperty("是否全员房间")
    private Boolean hotFlag;
    @ApiModelProperty("最新活跃时间")
    private Date activeTime;
    @ApiModelProperty("最新消息id")
    private Long lastMsgId;
    @ApiModelProperty("好友id(仅针对私聊设置)")
    private Long userId;
    @ApiModelProperty("房间状态 0：正常 1：禁用")
    private Integer status;
}
