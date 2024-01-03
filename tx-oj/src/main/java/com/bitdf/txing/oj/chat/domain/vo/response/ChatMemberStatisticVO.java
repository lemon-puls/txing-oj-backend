package com.bitdf.txing.oj.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/2 23:22:42
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMemberStatisticVO {
    @ApiModelProperty("在线人数")
    private Long onlineNum;
}
