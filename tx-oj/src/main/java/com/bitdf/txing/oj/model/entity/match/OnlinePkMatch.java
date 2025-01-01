package com.bitdf.txing.oj.model.entity.match;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tx_oj_match_onlinepk")
public class OnlinePkMatch {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户1 id
     */
    private Long userId1;
    /**
     * 用户2 id
     */
    private Long userId2;
    /**
     * 竞赛开始时间
     */
    private Date startTime;
    /**
     * 竞赛结束时间
     */
    private Date endTime;
    /**
     * 用户1提交时间
     */
    private Date submitTime1;
    /**
     * 用户2提交时间
     */
    private Date submitTime2;
    /**
     * 用户1提交记录
     */
    private Long submitId1;
    /**
     * 用户2提交记录
     */
    private Long submitId2;
    /**
     * 获胜者id
     */
    private Long winnerId;
    /**
     * 题目id
     */
    private Long questionId;
    /**
     * 竞赛状态(0: 未开始 1：进行中 2：已结束)
     */
    private Integer status;
    /**
     *  竞赛得分
     */
    private Double score1;
    /**
     *
     */
    private Double score2;
    /**
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
    private Integer isDelete;
}
