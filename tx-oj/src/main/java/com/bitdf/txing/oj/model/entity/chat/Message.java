package com.bitdf.txing.oj.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.bitdf.txing.oj.model.entity.chat.msg.MessageExtra;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2023/12/28 10:18:07
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tx_oj_message")
public class Message {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 房间ID
     */
    private Long roomId;
    /**
     * 消息发送者ID
     */
    private Long fromUserId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息类型： 0：文本消息
     */
    private Integer type;
    /**
     * 消息扩展字段
     */
    @TableField(value = "extra", typeHandler = JacksonTypeHandler.class)
    private MessageExtra extra;
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
