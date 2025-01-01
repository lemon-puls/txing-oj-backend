package com.bitdf.txing.oj.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/28 10:09:41
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tx_oj_contact")
public class Contact {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 房间ID
     */
    private Long roomId;
    /**
     * 最后读取时间
     */
    private Date readTime;
    /**
     * 会话最后活跃时间
     */
    private Date activeTime;
    /**
     * 最新的消息ID
     */
    private Long msgId;
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
    /**
     * 状态：0：正常 1：移除
     */
    private Integer status;

}
