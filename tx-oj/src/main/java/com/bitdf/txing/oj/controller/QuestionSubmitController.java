package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.Map;


import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.QuestionSubmitService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;



/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-13 21:54:02
 */
@RestController
@RequestMapping("/question/submit")
public class QuestionSubmitController {
    @Autowired
    private QuestionSubmitService questionSubmitService;

    /**
     * 分页查询
     */
    @PostMapping("/list")
    public R list(@RequestBody PageVO queryVO){
        PageUtils page = questionSubmitService.queryPage(queryVO);
        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		QuestionSubmit questionSubmit = questionSubmitService.getById(id);

        return R.ok().put("questionSubmit", questionSubmit);
    }

    /**
     * 提交作答
     */
    @RequestMapping("/do")
    @AuthCheck(mustRole = "login")
    public R save(@RequestBody QuestionSubmitDoRequest questionSubmitDoRequest){
		questionSubmitService.doSubmit(questionSubmitDoRequest);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody QuestionSubmit questionSubmit){
		questionSubmitService.updateById(questionSubmit);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		questionSubmitService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
