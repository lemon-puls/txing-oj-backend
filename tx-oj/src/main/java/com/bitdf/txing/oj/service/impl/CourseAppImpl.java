package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.CourseMapper;
import com.bitdf.txing.oj.model.dto.course.CourseAddRequest;
import com.bitdf.txing.oj.model.dto.course.CourseBaseUpdateRequest;
import com.bitdf.txing.oj.model.dto.course.CourseVideoUpdateOrAddRequest;
import com.bitdf.txing.oj.model.entity.course.Course;
import com.bitdf.txing.oj.model.entity.course.CourseFavour;
import com.bitdf.txing.oj.model.entity.course.CourseVideo;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.course.CourseSearchItemVO;
import com.bitdf.txing.oj.model.vo.course.CourseVO;
import com.bitdf.txing.oj.model.vo.course.CourseVideoPlayVO;
import com.bitdf.txing.oj.model.vo.course.CourseVideoVO;
import com.bitdf.txing.oj.service.CourseAppService;
import com.bitdf.txing.oj.service.CourseFavourService;
import com.bitdf.txing.oj.service.CourseService;
import com.bitdf.txing.oj.service.CourseVideoService;
import com.bitdf.txing.oj.service.adapter.CourseAdapter;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourseAppImpl implements CourseAppService {

    @Autowired
    CourseService courseService;
    @Autowired
    CourseVideoService courseVideoService;
    @Autowired
    CourseFavourService courseFavourService;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CourseAdapter courseAdapter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCourse(CourseAddRequest request, Long userId) {
        CourseVO courseVO = request.getCourseVO();
        List<CourseVideoVO> videoVOList = request.getVideoVOList();
        // 计算视频总时长
        long totalTimes = videoVOList.stream().mapToLong(CourseVideoVO::getTimes).sum();
        courseVO.setTimes(totalTimes);
        // 设置视频作者ID
        courseVO.setUserId(userId);
        // 设置课程小节数
        courseVO.setNoduleCount(videoVOList.size());
        // 初始化收藏数
        courseVO.setFavourCount(0);
        // 保存课程汇总信息
        Course course = CourseAdapter.buildCourseByCourseVO(courseVO);
        courseService.save(course);
        // 为各小节设置课程总表记录id
        List<CourseVideo> collect = videoVOList.stream().map(video -> {
            video.setCourseId(course.getId());
            return CourseAdapter.buildCourseVideoByVideoVO(video);
        }).collect(Collectors.toList());
        // 保存小节信息
        courseVideoService.saveBatch(collect);
        return course.getId();
    }

    /**
     * 分页查询
     *
     * @param queryVO
     * @return
     */
    @Override
    public PageUtils queryPage(PageVO queryVO) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        IPage<Course> iPage = new Query<Course>().buildWrapperAndPage(wrapper, queryVO, null);
        IPage<Course> page = courseService.page(iPage, wrapper);
        return new PageUtils(page);
    }

    @Override
    public CourseVideoPlayVO getVideoPlayVO(Long courseId) {
        Course course = courseService.getById(courseId);
        ThrowUtils.throwIf(course == null, "课程不存在");
        List<CourseSearchItemVO> items = courseService.getCourseSearchItemVOsByCourse(Arrays.asList(course));
        CourseSearchItemVO searchItemVO = CollectionUtil.getFirst(items);
        CourseVideoPlayVO playVO = new CourseVideoPlayVO();
        BeanUtils.copyProperties(searchItemVO, playVO);
        // 获取各小节信息
        List<CourseVideo> courseVideos = courseVideoService.listByCourseId(courseId);
        if (!courseVideos.isEmpty()) {
            List<CourseVideoVO> videoVOS = CourseAdapter.buildCourseVideoVOsByVideo(courseVideos);
            playVO.setVideoVOS(videoVOS);
        }
        return playVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourses(Long[] courseIds, Long userId) {
        List<Course> list = courseService.getCoursesByIdAndUserId(courseIds, userId);
        if (list.isEmpty()) {
            return;
        }
        List<Long> deleteIds = list.stream().map(course -> course.getId()).collect(Collectors.toList());
        // 删除课程总表记录
        courseService.removeByIds(deleteIds);
        // 删除小节记录
        courseVideoService.deleteByCourseIds(deleteIds);
    }

    /**
     * 更新课程基本信息
     *
     * @param request
     * @param userId
     */
    @Override
    public void updateCourseBaseInfo(CourseBaseUpdateRequest request, Long userId) {
        Course course = courseService.getById(request.getId());
        ThrowUtils.throwIf(course == null || !userId.equals(course.getUserId()), "该课程不存在");
        Course update = new Course();
        BeanUtils.copyProperties(request, update);
        boolean b = courseService.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrAddVideo(CourseVideoUpdateOrAddRequest request, Long userId) {
        // 合法性校验
        Course course = courseService.getById(request.getCourseId());
        ThrowUtils.throwIf(course == null || !userId.equals(course.getUserId()), "课程不存在");
        // 更新 or 添加
        CourseVideo courseVideo = new CourseVideo();
        BeanUtils.copyProperties(request, courseVideo);
        Integer noduleCount;
        Long times;
        if (Objects.isNull(courseVideo.getId())) {
            // 执行插入操作
            boolean save = courseVideoService.save(courseVideo);
            noduleCount = 1;
            times = courseVideo.getTimes();
            courseService.increaseCourseTimeAndNoduleCount(course.getId(), times, noduleCount);
        } else {
//            执行更新操作
            CourseVideo oldVideo = courseVideoService.getById(courseVideo.getId());
            ThrowUtils.throwIf(oldVideo == null || !course.getId().equals(oldVideo.getCourseId()), "视频不存在");
            courseVideoService.updateById(courseVideo);
            noduleCount = 0;
            times = courseVideo.getTimes() - oldVideo.getTimes();
            courseService.increaseCourseTimeAndNoduleCount(course.getId(), times, noduleCount);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideoBatch(Long courseId, Long[] videoIds, Long userId) {
        // 合法性校验
        Course course = courseService.getById(courseId);
        ThrowUtils.throwIf(course == null || !userId.equals(course.getUserId()), "课程不存在");
        // 删除
        LambdaQueryWrapper<CourseVideo> wrapper = new QueryWrapper<CourseVideo>()
                .lambda()
                .in(CourseVideo::getId, videoIds)
                .eq(CourseVideo::getCourseId, courseId);
//        int count = courseVideoService.count(wrapper);
        List<CourseVideo> courseVideos = courseVideoService.list(wrapper);
        boolean remove = courseVideoService.remove(wrapper);
        // 更新课程基本数据
        if (remove) {
            Long times = courseVideos.stream().mapToLong(CourseVideo::getTimes).sum();
            Integer noduleCount = courseVideos.size();
            courseService.reduceCourseTimeAndNoduleCount(courseId, times, noduleCount);
        }
    }

    @Override
    public int doFavour(Long courseId, Long userId) {
        // 判断是否存在
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        // 是否已帖子收藏
        // 每个用户串行帖子收藏
        // 锁必须要包裹住事务方法
        CourseAppService courseAppService = (CourseAppService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return courseAppService.doFavourInner(userId, courseId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doFavourInner(long userId, long courseId) {
        CourseFavour courseFavour = new CourseFavour();
        courseFavour.setCourseId(courseId);
        courseFavour.setUserId(userId);

        QueryWrapper<CourseFavour> wrapper = new QueryWrapper<>(courseFavour);

        boolean result;
        CourseFavour oldCourseFavour = courseFavourService.getOne(wrapper);
        // 已收藏
        if (oldCourseFavour != null) {
            result = courseFavourService.remove(wrapper);
            if (result) {
                // 帖子收藏数 - 1
                result = courseService.update(new UpdateWrapper<Course>().lambda()
                        .eq(Course::getId, courseId)
                        .gt(Course::getFavourCount, 0)
                        .setSql("favour_count = favour_count - 1, update_time = NOW()"));
                return result ? -1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        } else {
            // 未帖子收藏
            result = courseFavourService.save(courseFavour);
            if (result) {
                // 帖子收藏数 + 1
                result = courseService.update()
                        .eq("id", courseId)
                        .setSql("favour_count = favour_count + 1, update_time = NOW()")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        }
    }

    @Override
    public PageUtils getUserFavour(Long userId, PageRequest pageRequest) {
        Page<Course> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        Page<Course> page1 = courseMapper.getUserFavourPage(page, userId, pageRequest);
        List<CourseSearchItemVO> itemVOS = courseAdapter.buildCourseSearchItemVOsByCourses(page1.getRecords());
        PageUtils pageUtils = new PageUtils(page1);
        pageUtils.setList(itemVOS);
        return pageUtils;
    }
}
