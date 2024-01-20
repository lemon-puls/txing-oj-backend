package com.bitdf.txing.oj.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/28 9:58:58
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "tx_oj_room_group")
public class RoomGroup {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 房间id
     */
    private Long roomId;
    /**
     * 群名称
     */
    private String name;
    /**
     * 群头像
     */
    private String avatar;
    /**
     * 额外信息
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
