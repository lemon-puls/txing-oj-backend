package com.bitdf.txing.oj.model.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/28 19:35:29
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tx_oj_user_friend")
public class UserFriend {

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
     * 好友id
     */
    private Long friendId;
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
