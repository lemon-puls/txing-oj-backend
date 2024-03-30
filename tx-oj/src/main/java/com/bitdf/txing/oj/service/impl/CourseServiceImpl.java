package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.mapper.CourseMapper;
import com.bitdf.txing.oj.model.entity.course.Course;
import com.bitdf.txing.oj.model.entity.course.CourseFavour;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.course.CourseSearchItemVO;
import com.bitdf.txing.oj.service.CourseFavourService;
import com.bitdf.txing.oj.service.CourseService;
import com.bitdf.txing.oj.service.adapter.CourseAdapter;
import com.bitdf.txing.oj.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("courseService")
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    UserCache userCache;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CourseFavourService courseFavourService;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<CourseCourseEntity> page = this.page(
//                new Query<CourseCourseEntity>().getPage(params),
//                new QueryWrapper<CourseCourseEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public List<CourseSearchItemVO> getCourseSearchItemVOsByCourse(List<?> list) {
        User loginUser = AuthInterceptor.userThreadLocal.get();
        List<CourseSearchItemVO> collect = list.stream().map(item -> {
            Course course = (Course) item;
            User user = userCache.get(course.getUserId());
            // 判断是否已收藏
            int count = courseFavourService.count(new QueryWrapper<CourseFavour>().lambda()
                    .eq(CourseFavour::getCourseId, course.getId())
                    .eq(CourseFavour::getUserId, loginUser.getId()));
            boolean isFavour = count > 0 ? true : false;
            CourseSearchItemVO courseSearchItemVO = CourseAdapter.buildCourseSearchItemVOByCourse(course, user, isFavour);
            return courseSearchItemVO;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<Course> getCoursesByIdAndUserId(Long[] courseIds, Long userId) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(Course::getUserId, userId)
                .in(Course::getId, courseIds);
        return this.list(wrapper);
    }

    @Override
    public void increaseCourseTimeAndNoduleCount(Long courseId, Long times, Integer noduleCount) {
        courseMapper.increaseCourseTimeAndNoduleCount(courseId, times, noduleCount);
    }

    @Override
    public void reduceCourseTimeAndNoduleCount(Long courseId, Long times, Integer noduleCount) {
        courseMapper.reduceCourseTimeAndNoduleCount(courseId, times, noduleCount);
    }
}