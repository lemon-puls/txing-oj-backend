package com.bitdf.txing.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitdf.txing.oj.model.entity.course.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author lizhiwei
 * @email
 * @date 2024-03-22 21:27:47
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    void reduceCourseTimeAndNoduleCount(@Param("courseId") Long courseId, @Param("times") Long times,
                                        @Param("noduleCount") Integer noduleCount);

    void increaseCourseTimeAndNoduleCount(@Param("courseId") Long courseId, @Param("times") Long times,
                                          @Param("noduleCount") Integer noduleCount);
}
