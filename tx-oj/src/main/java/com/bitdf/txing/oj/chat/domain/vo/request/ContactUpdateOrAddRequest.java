package com.bitdf.txing.oj.chat.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/29 17:49:32
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactUpdateOrAddRequest {

    private Long roomId;

    private Long friendId;
}
