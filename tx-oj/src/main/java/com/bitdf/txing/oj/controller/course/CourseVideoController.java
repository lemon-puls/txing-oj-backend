package com.bitdf.txing.oj.controller.course;

import com.bitdf.txing.oj.model.entity.course.CourseVideo;
import com.bitdf.txing.oj.service.CourseVideoService;
import com.bitdf.txing.oj.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;



/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2024-03-22 21:27:47
 */
@RestController
@RequestMapping("oj/coursevideo")
public class CourseVideoController {
    @Autowired
    private CourseVideoService courseVideoService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = courseVideoService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }
//

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CourseVideo courseVideo = courseVideoService.getById(id);

        return R.ok().put("courseVideo", courseVideo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CourseVideo courseVideo){
		courseVideoService.save(courseVideo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CourseVideo courseVideo){
		courseVideoService.updateById(courseVideo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		courseVideoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
