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
@TableName("tx_oj_course_video")
public class CourseVideo {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程总表记录id
     */
    private Long courseId;
    /**
     * 名称
     */
    private String name;
    /**
     * 序号
     */
    private Integer orderNo;
    /**
     * 时长
     */
    private Long times;
    /**
     * 封面URL
     */
    private String coverUrl;
    /**
     * 视频URL
     */
    private String videoUrl;
    /**
     * fileId
     */
    private Long fileId;

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
