package com.bitdf.txing.oj.model.vo.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/12/4 20:49:05
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateVO {
    /**
     * 标题
     */
    private String title;
    /**
     * 摘要
     */
    private String intro;
    /**
     * 内容
     */
    private String content;
    /**
     * 封面图
     */
    private String coverImg;
}
