package com.bitdf.txing.oj.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/30 10:54:52
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsFriendApplyAgreeVO {
    @ApiModelProperty("好友Id")
    private Long userId;
    @ApiModelProperty("在线状态 0：不在线 1：在线")
    private Integer activeStatus;
}
