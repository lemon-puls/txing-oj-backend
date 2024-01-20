package com.bitdf.txing.oj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.util.anno.MysqlColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@TableName(value = "tx_oj_post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;
    /**
     * 摘要
     */
    private String intro;
    /**
     * 内容
     */
    private String content;
    /**
     * 封面图
     */
    private String coverImg;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;
    /**
     * 评论数
     */
    private Integer commentNum;
    /**
     * 创建用户 id
     */
    private Long userId;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
