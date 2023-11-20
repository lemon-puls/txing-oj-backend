package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.model.entity.QuestionFavour;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.QuestionFavourService;


/**
 * @author lizhiwei
 * @email
 * @date 2023-11-20 19:18:19
 */
@RestController
@RequestMapping("/question/favour")
public class QuestionFavourController {
    @Autowired
    private QuestionFavourService questionFavourService;

    /**
     * 分页查询(只返回当前登录用户的收藏题目)
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = "login")
    public R list(@RequestBody PageVO queryVO) {
        PageUtils page = questionFavourService.queryPage(queryVO);
        return R.ok().put("data", page);
    }


//    /**
//     * 信息
//     */
//    @GetMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id) {
//        QuestionFavour questionFavour = questionFavourService.getById(id);
//
//        return R.ok().put("questionFavour", questionFavour);
//    }
//
//    /**
//     * 保存
//     */
//    @PostMapping("/save")
//    public R save(@RequestBody QuestionFavour questionFavour) {
//        questionFavourService.save(questionFavour);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @PostMapping("/update")
//    public R update(@RequestBody QuestionFavour questionFavour) {
//        questionFavourService.updateById(questionFavour);
//
//        return R.ok();
//    }

//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids) {
//        questionFavourService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

    /**
     * 题目收藏与取消
     *
     * @param questionId
     * @return
     */
    @GetMapping("/favour")
    @AuthCheck(mustRole = "login")
    public R favourQuestion(@RequestParam("questionId") Long questionId) {
        // true: 收藏 false: 取消
        Boolean b = questionFavourService.favourQuestion(questionId);
        return R.ok(b);
    }
}
