package com.bitdf.txing.oj.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/28 20:02:44
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberRemoveRequest {

    @ApiModelProperty("房间id")
    private Long roomId;
    @ApiModelProperty("要移除的用户id（当不指定时 默认选定当前用户 当选定的用户是群主时 即表示解散该群 ）")
    private Long userId;
}
