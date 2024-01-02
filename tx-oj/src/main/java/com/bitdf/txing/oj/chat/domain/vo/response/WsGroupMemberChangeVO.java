package com.bitdf.txing.oj.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2024/1/2 15:06:35
 * 注释：
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WsGroupMemberChangeVO {
    /**
     * 新添成员
     */
    public static final Integer CHANGE_TYPE_ADD = 0;
    /**
     * 移除成员
     */
    public static final Integer CHANGE_TYPE_RREMOVE = 1;

    @ApiModelProperty("房间id")
    private Long roomId;
    @ApiModelProperty("变动userId")
    private Long userId;
    @ApiModelProperty("变动类型 0：新添 1：移除")
    private Integer changeType;
    @ApiModelProperty("在线状态：0：离线 1：在线")
    private Integer activeStatus;
    @ApiModelProperty("最后一次上下线时间")
    private Date lastOpsTime;

}
