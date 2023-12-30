package com.bitdf.txing.oj.model.vo.user;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/12/30 11:14:55
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendVO {

    @ApiModelProperty("好友Id")
    private Long userId;
    @ApiModelProperty("在线状态 0：不在线 1：在线")
    private Integer activeStatus;
}
