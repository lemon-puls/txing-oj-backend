package com.bitdf.txing.oj.model.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseBaseUpdateRequest {
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
     * 课程简介
     */
    private String intro;
}
