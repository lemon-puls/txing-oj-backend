package com.bitdf.txing.oj.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2024/1/2 11:39:07
 * 注释：
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GroupAddRequest {

    @ApiModelProperty("群聊名称")
    private String name;
    @ApiModelProperty("群聊成员id集合")
    private List<Long> userIdList;
    @ApiModelProperty("群聊头像")
    private String groupAvatar;

}
