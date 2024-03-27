package com.bitdf.txing.oj.model.vo.course;

import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseSearchItemVO {
    /**
     * id
     */
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
     * 作者信息
     */
    private UserShowVO userShowVO;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
