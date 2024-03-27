package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.course.CourseVideo;

import java.util.List;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2024-03-22 21:27:47
 */
public interface CourseVideoService extends IService<CourseVideo> {
    List<CourseVideo> listByCourseId(Long courseId);

    void deleteByCourseIds(List<Long> courseIds);


//    PageUtils queryPage(Map<String, Object> params);
}

