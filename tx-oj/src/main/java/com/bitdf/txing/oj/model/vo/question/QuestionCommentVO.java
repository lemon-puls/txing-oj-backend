package com.bitdf.txing.oj.model.vo.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/11/17 15:27:54
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCommentVO {
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 是否点赞
     */
    private Boolean isFavour;
}
