package com.bitdf.txing.oj.controller.course;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.config.VodSigner;
import com.bitdf.txing.oj.model.dto.course.CourseAddRequest;
import com.bitdf.txing.oj.model.dto.course.CourseBaseUpdateRequest;
import com.bitdf.txing.oj.model.dto.course.CourseVideoUpdateOrAddRequest;
import com.bitdf.txing.oj.model.vo.course.CourseSearchItemVO;
import com.bitdf.txing.oj.model.vo.course.CourseVideoPlayVO;
import com.bitdf.txing.oj.service.CourseAppService;
import com.bitdf.txing.oj.service.CourseService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseAppController {

    @Autowired
    VodSigner vodSigner;
    @Autowired
    CourseAppService courseAppService;
    @Autowired
    CourseService courseService;

    /**
     * 获取视频上传签名
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/sign/get")
    @AuthCheck(mustRole = "login")
    public R getVodSign() throws Exception {
        String signature = vodSigner.getUploadSignature();
        return R.ok().put("data", signature);
    }

    /**
     * 添加课程
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "login")
    public R addCourse(@RequestBody CourseAddRequest request) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        Long courseId = courseAppService.addCourse(request, userId);
        return R.ok(courseId);
    }

    /**
     * 课程分页查询
     */
    @PostMapping("/page/search")
    public R searchCourseByPage(@RequestBody PageVO queryVO) {
        PageUtils page = courseAppService.queryPage(queryVO);
        if (!page.getList().isEmpty()) {
            List<CourseSearchItemVO> list = courseService.getCourseSearchItemVOsByCourse(page.getList());
            page.setList(list);
        }
        return R.ok().put("data", page);
    }

    /**
     * 获取课程播放所需信息
     */
    @GetMapping("/video/play/info/get")
    public R getVideoPlayVO(@RequestParam("courseId") Long courseId) {
        CourseVideoPlayVO playVO = courseAppService.getVideoPlayVO(courseId);
        return R.ok(playVO);
    }

    /**
     * 获取视频播放签名
     */
    @GetMapping("/play/sign/get")
    public R getPlaySign(@RequestParam("fileId") String fileId) {
        Integer AppId = 1311424669; // 用户 appid
        String FileId = fileId; // 目标 FileId
        String AudioVideoType = "Original"; // 播放的音视频类型
        Integer RawAdaptiveDefinition = 10; // 允许输出的未加密的自适应码流模板 ID
        Integer ImageSpriteDefinition = 10; // 做进度条预览的雪碧图模板 ID
        Integer CurrentTime = 1589448067;
//        Long PsignExpire = CurrentTime + 1000 * 60 * 60 * 2; // 可任意设置过期时间
//        String UrlTimeExpire = "5ebe9423‬"; // 可任意设置过期时间
        String PlayKey = "CcqpJzaiTIuCoFBZtH4c";
        HashMap<String, Object> urlAccessInfo = new HashMap<String, Object>();
//        urlAccessInfo.put("t", UrlTimeExpire);
        HashMap<String, Object> contentInfo = new HashMap<String, Object>();
        contentInfo.put("audioVideoType", AudioVideoType);
//        contentInfo.put("rawAdaptiveDefinition", RawAdaptiveDefinition);
//        contentInfo.put("imageSpriteDefinition", ImageSpriteDefinition);


        try {
            Algorithm algorithm = Algorithm.HMAC256(PlayKey);
            String token = JWT.create().withClaim("appId", AppId).withClaim("fileId", FileId)
                    .withClaim("contentInfo", contentInfo)
                    .withClaim("currentTimeStamp", CurrentTime)
                    .withClaim("urlAccessInfo", urlAccessInfo).sign(algorithm);
            System.out.println("token:" + token);
            return R.ok().put("data", token);
        } catch (JWTCreationException exception) {
            // Invalid Signing configuration / Couldn't convert Claims.
        }
        return R.error();
    }

    /**
     * 删除用户课程
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = "login")
    public R deleteCourses(@RequestBody Long[] courseIds) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        courseAppService.deleteCourses(courseIds, userId);
        return R.ok();
    }

    /**
     * 更新视频基本信息
     */
    @PostMapping("base/update")
    @AuthCheck(mustRole = "login")
    public R updateCourseBaseInfo(@RequestBody CourseBaseUpdateRequest request) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        courseAppService.updateCourseBaseInfo(request, userId);
        return R.ok();
    }

    /**
     * 更新或添加课程小节信息
     */
    @PostMapping("/video/change")
    @AuthCheck(mustRole = "login")
    public R addOrUpdateCourseVideo(@RequestBody CourseVideoUpdateOrAddRequest request) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        courseAppService.updateOrAddVideo(request, userId);
        return R.ok();
    }

    /**
     * 删除课程小节
     */
    @PostMapping("/video/delete/batch")
    @AuthCheck(mustRole = "login")
    public R deleteVideoBatch(@RequestBody Long[] videoIds, @RequestParam("courseId") Long courseId){
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        courseAppService.deleteVideoBatch(courseId, videoIds, userId);
        return R.ok();
    }
}
