package com.bitdf.txing.oj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 题目
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tx_oj_question")
public class Question {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 题目内容
     */
    private String content;
    /**
     * 题目标签
     */
    private String tags;
    /**
     * 题目答案
     */
    private String answer;
    /**
     * 提交次数
     */
    private Integer submitNum;
    /**
     * 通过次数
     */
    private Integer acceptedNum;
    /**
     * 判题用例
     */
    private String judgeCase;
    /**
     * 题目配置
     */
    private String judgeConfig;
    /**
     * 点赞数
     */
    private Integer thumbNum;
    /**
     * 收藏数
     */
    private Integer favourNum;
    /**
     * 题目创建人
     */
    private Long userId;
//    /**
//     * 难易程度 可考虑扩展
//     */
//    private Integer easy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    @MysqlColumn(defaultValue = "0")
    private Integer isDelete;


}
