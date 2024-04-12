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
@TableName("tx_oj_match_submit_relate")
public class MatchSubmitRelate {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 竞赛id
     */
    private Long matchId;
    /**
     * 参加记录id
     */
    private Long joinRecordId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 竞赛类型（0: 在线PK 1：周赛）
     */
    private Integer matchType;
    /**
     * 提交id
     */
    private Long submitId;
    /**
     * 题目id
     */
    private Long questionId;

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
