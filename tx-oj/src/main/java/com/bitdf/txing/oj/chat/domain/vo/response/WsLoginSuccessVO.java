package com.bitdf.txing.oj.chat.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/9 11:32:28
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsLoginSuccessVO {
    private Long userId;
    private String avatar;
    private String token;
    private String userName;
}
