package com.bitdf.txing.oj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 题目评论
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tx_oj_question_comment")
public class QuestionComment {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 目标题目Id
     */
    private Long questionId;
    /**
     * 点赞数
     */
    private Integer favourNum;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
