package com.bitdf.txing.oj.model.entity.match;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("txing_oj_match_user_relate")
public class MatchUserRelate {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     *  竞赛id
     */
    private Long matchId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 参赛方式（0：正式 1：模拟）
     */
    private Integer joinType;
    /**
     * 成绩排名
     */
    private Integer gradeRank;
    /**
     * 获得分数
     */
    private Integer score;
    /**
     * 判题状态（0：未完成 1：已完成）
     */
    private Integer judgeStatus;
    /**
     * Ac题目数
     */
    private Integer acCount;
    /**
     * 未AC题目通过用例和
     */
    private Integer unAcRateSum;
    /**
     * 进入赛场时间
     */
    private Date startTime;
    /**
     * 提交作答时间
     */
    private Date endTime;

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
