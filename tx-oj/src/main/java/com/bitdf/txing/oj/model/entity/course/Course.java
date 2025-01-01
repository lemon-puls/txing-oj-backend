package com.bitdf.txing.oj.model.entity.course;

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
@TableName("tx_oj_course_course")
public class Course {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 课程名称
     */
    private String name;
    /**
     * 课程封面
     */
    private String coverUrl;
    /**
     * 课程时长
     */
    private Long times;
    /**
     * 课程小节数
     */
    private Integer noduleCount;
    /**
     * 课程被收藏数
     */
    private Integer favourCount;
    /**
     * 课程作者id
     */
    private Long userId;
    /**
     * 课程简介
     */
    private String intro;
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
    private Integer status;

    private String remark;
}
