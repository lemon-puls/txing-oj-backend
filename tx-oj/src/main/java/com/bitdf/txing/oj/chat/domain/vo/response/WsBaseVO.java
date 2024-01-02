package com.bitdf.txing.oj.chat.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:21:40
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsBaseVO<T> {
    /**
     * 类型
     */
    private Integer type;
    /**
     * 内容
     */
    private T data;
}
