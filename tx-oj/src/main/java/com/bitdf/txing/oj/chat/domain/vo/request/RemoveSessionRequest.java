package com.bitdf.txing.oj.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/29 14:48:09
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoveSessionRequest {
    @ApiModelProperty("房间id")
    private Long roomId;
}
