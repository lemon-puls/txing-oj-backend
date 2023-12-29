package com.bitdf.txing.oj.chat.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/29 16:33:58
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextMessageRequest {
    /**
     * 文本内容
     */
    private String content;
    /**
     * 艾特的用户ID集合
     */
    private List<Long> atUserIds;
}
