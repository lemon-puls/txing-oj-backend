package com.bitdf.txing.oj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 题目提交
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tx_oj_question_submit")
public class QuestionSubmit {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 编程语言
     */
    private String language;
    /**
     * 用户代码
     */
    private String code;
    /**
     * 判题信息
     */
    private String judgeInfo;
    /**
     * 判题状态 0：待判题 1： 判题中 2：成功 3：失败
     */
    private Integer status;
    /**
     * 题目ID
     */
    private Long questionId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 超过百分之多少的人
     */
    private Float exceedPercent;
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
