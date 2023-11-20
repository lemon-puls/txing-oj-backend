package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.model.entity.QuestionFavour;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitdf.txing.oj.service.QuestionFavourService;


/**
 * @author lizhiwei
 * @email
 * @date 2023-11-20 19:18:19
 */
@RestController
@RequestMapping("oj/questionfavour")
public class QuestionFavourController {
    @Autowired
    private QuestionFavourService questionFavourService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = questionFavourService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        QuestionFavour questionFavour = questionFavourService.getById(id);

        return R.ok().put("questionFavour", questionFavour);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody QuestionFavour questionFavour) {
        questionFavourService.save(questionFavour);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody QuestionFavour questionFavour) {
        questionFavourService.updateById(questionFavour);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        questionFavourService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
