package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.CourseVideoMapper;
import com.bitdf.txing.oj.model.entity.course.CourseVideo;
import com.bitdf.txing.oj.service.CourseVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("courseVideoService")
public class CourseVideoServiceImpl extends ServiceImpl<CourseVideoMapper, CourseVideo> implements CourseVideoService {

    @Autowired
    CourseVideoMapper courseVideoMapper;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<CourseVideoEntity> page = this.page(
//                new Query<CourseVideoEntity>().getPage(params),
//                new QueryWrapper<CourseVideoEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public List<CourseVideo> listByCourseId(Long courseId) {
        QueryWrapper<CourseVideo> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(CourseVideo::getCourseId, courseId)
                .orderByAsc(CourseVideo::getOrderNo);
        return this.list(wrapper);
    }

    @Override
    public void deleteByCourseIds(List<Long> courseIds) {
        this.remove(new QueryWrapper<CourseVideo>().lambda().in(CourseVideo::getCourseId, courseIds));
    }

}