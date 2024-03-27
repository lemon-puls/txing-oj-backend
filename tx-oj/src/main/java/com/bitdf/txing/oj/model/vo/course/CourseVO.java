package com.bitdf.txing.oj.model.vo.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseVO {
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
}
