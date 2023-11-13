package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.model.entity.QuestionComment;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.QuestionCommentService;


/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-13 21:54:02
 */
@RestController
@RequestMapping("oj/questioncomment")
public class QuestionCommentController {
    @Autowired
    private QuestionCommentService questionCommentService;

    /**
     * 分页查询
     */
    @PostMapping("/list")
    public R list(@RequestBody PageVO queryVO){
        PageUtils page = questionCommentService.queryPage(queryVO);
        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		QuestionComment questionComment = questionCommentService.getById(id);

        return R.ok().put("questionComment", questionComment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody QuestionComment questionComment){
		questionCommentService.save(questionComment);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody QuestionComment questionComment){
		questionCommentService.updateById(questionComment);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		questionCommentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
