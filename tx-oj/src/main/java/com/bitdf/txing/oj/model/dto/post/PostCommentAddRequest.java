package com.bitdf.txing.oj.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/12/3 1:07:43
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentAddRequest {
    /**
     * 评论内容
     */
    private String content;
    /**
     * 文章Id
     */
    private Long postId;

}

