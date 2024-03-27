package com.bitdf.txing.oj.model.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicCommentRequest {
    /**
     * id
     */
    private Long id;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 回复评论Id
     */
    private Long replyId;
    /**
     * 帖子Id
     */
    private Long topicId;

}
