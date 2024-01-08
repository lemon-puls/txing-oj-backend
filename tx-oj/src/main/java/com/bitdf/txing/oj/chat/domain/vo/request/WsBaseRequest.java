package com.bitdf.txing.oj.chat.domain.vo.request;

import lombok.Data;

/**
 * @author Lizhiwei
 * @date 2024/1/8 0:06:44
 * 注释：
 */
@Data
public class WsBaseRequest {
    /**
     * ws消息类型
     */
    private Integer type;
    /**
     * 具体内容
     */
    private String data;
}
