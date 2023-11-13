package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.Map;


import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.QuestionService;




/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-13 21:54:02
 */
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    /**
     * 分页查询
     */
    @PostMapping("/list")
    public R list(@RequestBody PageVO queryVO){
        PageUtils page = questionService.queryPage(queryVO);
        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		Question question = questionService.getById(id);

        return R.ok().put("question", question);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody Question question){
		questionService.save(question);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody Question question){
		questionService.updateById(question);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		questionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
