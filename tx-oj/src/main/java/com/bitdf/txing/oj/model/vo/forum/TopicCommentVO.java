package com.bitdf.txing.oj.model.vo.forum;

import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicCommentVO {
    /**
     * id
     */
    private Long id;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论用户
     */
    private Long userId;
    /**
     * 回复评论Id
     */
    private Long replyId;
    /**
     * 帖子Id
     */
    private Long topicId;
    /**
     * 点赞数
     */
    private Integer favourNum;
    /**
     * 评论用户信息
     */
    private UserShowVO userShowVO;
    /**
     * 回复用户名
     */
    private String userName;

    /**
     * 回复评论列表
     */
    private List<TopicCommentVO> replyList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
