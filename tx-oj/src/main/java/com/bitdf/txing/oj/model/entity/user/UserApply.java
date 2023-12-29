package com.bitdf.txing.oj.model.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/28 19:21:07
 * 注释：
 */
@TableName(value = "tx_oj_user_apply")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserApply {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 申请人id
     */
    private Long userId;
    /**
     * 申请类型 0：加好友
     */
    private Integer type;
    /**
     * 目标id（例如：接受方用户id）
     */
    private Long targetId;
    /**
     * 申请内容
     */
    private String msg;
    /**
     * 申请状态：0：待通过 1：已通过
     */
    private Integer status;
    /**
     * 阅读状态：0：未读 1：已读
     */
    private Integer readStatus;

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
