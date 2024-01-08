package com.bitdf.txing.oj.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/3 11:26:01
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsAuthorize {
    private String token;
    private Long userId;
}
