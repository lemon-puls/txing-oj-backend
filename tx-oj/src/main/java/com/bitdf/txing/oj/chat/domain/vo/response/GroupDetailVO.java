package com.bitdf.txing.oj.chat.domain.vo.response;

import com.bitdf.txing.oj.chat.enume.GroupRoleEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/1 13:14:45
 * 注释：
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailVO {
    @ApiModelProperty("房间id")
    private Long roomId;
    @ApiModelProperty("群名称")
    private String groupName;
    @ApiModelProperty("群头像")
    private String avatar;
    @ApiModelProperty("在线人数")
    private Long onlineCount;
    /**
     * @see GroupRoleEnum
     */
    @ApiModelProperty("当前用户角色：0:群主 1：管理员 2：普通成员 3：被踢出群聊")
    private Integer role;
}
