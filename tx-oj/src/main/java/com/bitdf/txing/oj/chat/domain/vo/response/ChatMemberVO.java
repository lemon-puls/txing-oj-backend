package com.bitdf.txing.oj.chat.domain.vo.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2024/1/1 20:21:50
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMemberVO {
    @ApiModelProperty("用户ID")
    private Long userId;
    /**
     * @see com.bitdf.txing.oj.model.enume.UserActiveStatusEnum
     */
    @ApiModelProperty("是否在线")
    private Integer activeStatus;
    @ApiModelProperty("最近一次上下线时间")
    private Date lastOpsTime;
}
