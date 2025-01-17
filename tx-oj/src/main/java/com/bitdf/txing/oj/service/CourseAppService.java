package com.bitdf.txing.oj.service;

import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.model.dto.course.CourseAddRequest;
import com.bitdf.txing.oj.model.dto.course.CourseBaseUpdateRequest;
import com.bitdf.txing.oj.model.dto.course.CourseVideoUpdateOrAddRequest;
import com.bitdf.txing.oj.model.vo.course.CourseVideoPlayVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.transaction.annotation.Transactional;

public interface CourseAppService {
    Long addCourse(CourseAddRequest request, Long userId);

    PageUtils queryPage(PageVO queryVO);

    CourseVideoPlayVO getVideoPlayVO(Long courseId);

    void deleteCourses(Long[] courseIds, Long userId);

    void updateCourseBaseInfo(CourseBaseUpdateRequest request, Long userId);

    void updateOrAddVideo(CourseVideoUpdateOrAddRequest request, Long userId);

    void deleteVideoBatch(Long courseId, Long[] videoIds, Long userId);

    int doFavour(Long courseId, Long userId);

    @Transactional(rollbackFor = Exception.class)
    int doFavourInner(long userId, long topicId);

    PageUtils getUserFavour(Long userId, PageRequest pageRequest);
}
