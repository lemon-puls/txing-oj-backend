package com.bitdf.txing.oj.model.vo.post;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/3 1:12:22
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentVO {
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
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 文章Id
     */
    private Long postId;
    /**
     * 点赞数
     */
    private Integer favourNum;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 是否点赞
     */
    private Boolean isFavour;

}
