package com.bitdf.txing.oj.service.adapter;

import com.bitdf.txing.oj.model.entity.course.Course;
import com.bitdf.txing.oj.model.entity.course.CourseVideo;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.course.CourseSearchItemVO;
import com.bitdf.txing.oj.model.vo.course.CourseVO;
import com.bitdf.txing.oj.model.vo.course.CourseVideoVO;
import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import com.bitdf.txing.oj.service.cache.UserCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseAdapter {

    @Autowired
    UserCache userCache;

    public static Course buildCourseByCourseVO(CourseVO courseVO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseVO, course);
        return course;
    }

    public static CourseVideo buildCourseVideoByVideoVO(CourseVideoVO videoVO) {
        CourseVideo courseVideo = new CourseVideo();
        BeanUtils.copyProperties(videoVO, courseVideo);
        return courseVideo;
    }

    public static List<CourseVideoVO> buildCourseVideoVOsByVideo(List<CourseVideo> list) {
        return list.stream().map(video -> {
            CourseVideoVO videoVO = new CourseVideoVO();
            BeanUtils.copyProperties(video, videoVO);
            return videoVO;
        }).collect(Collectors.toList());
    }

    public static CourseSearchItemVO buildCourseSearchItemVOByCourse(Course course, User user, boolean isFavour) {
        CourseSearchItemVO searchItemVO = new CourseSearchItemVO();
        BeanUtils.copyProperties(course, searchItemVO);
        UserShowVO userShowVO = new UserShowVO();
        BeanUtils.copyProperties(user, userShowVO);
        searchItemVO.setUserShowVO(userShowVO);
        searchItemVO.setFavour(isFavour);
        return searchItemVO;
    }

    public List<CourseSearchItemVO> buildCourseSearchItemVOsByCourses(List<?> courses) {
        return courses.stream().map(item -> {
            Course course = (Course) item;
            User user = userCache.get(course.getUserId());
            CourseSearchItemVO searchItemVO = buildCourseSearchItemVOByCourse(course, user, false);
            return searchItemVO;
        }).collect(Collectors.toList());
    }
}
