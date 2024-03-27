package com.bitdf.txing.oj.controller.course;

import com.bitdf.txing.oj.model.entity.course.CourseFavour;
import com.bitdf.txing.oj.service.CourseFavourService;
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
@RequestMapping("oj/coursefavour")
public class CourseFavourController {
    @Autowired
    private CourseFavourService courseFavourService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = courseFavourService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CourseFavour courseFavour = courseFavourService.getById(id);

        return R.ok().put("courseFavour", courseFavour);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CourseFavour courseFavour){
		courseFavourService.save(courseFavour);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CourseFavour courseFavour){
		courseFavourService.updateById(courseFavour);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		courseFavourService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
