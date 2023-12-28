package com.bitdf.txing.oj.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/28 8:58:08
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tx_oj_room")
public class Room {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 房间类型： 0：私聊 1：群聊
     */
    private Integer type;
    /**
     * 是否是总群
     */
    private Boolean hotFlag;
    /**
     * 群最后活跃时间
     */
    private Date activeTime;
    /**
     * 最新消息的消息id
     */
    private Long msgId;
    /**
     * 额外消息
     */
    private String extJson;
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
