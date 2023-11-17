package com.bitdf.txing.oj.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/11/17 15:16:22
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCommentAddRequest {
    /**
     * 评论内容
     */
    private String content;
    /**
     * 目标题目Id
     */
    private Long questionId;

}
