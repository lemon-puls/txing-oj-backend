package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import cn.hutool.json.JSONUtil;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.model.dto.question.QuestionVO;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.vo.question.QuestionSubmitDetailVO;
import com.bitdf.txing.oj.model.vo.question.QuestionSubmitSimpleVO;
import com.bitdf.txing.oj.service.QuestionService;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.QuestionSubmitService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;


/**
 * @author lizhiwei
 * @email
 * @date 2023-11-13 21:54:02
 */
@RestController
@RequestMapping("/question/submit")
public class QuestionSubmitController {
    @Autowired
    private QuestionSubmitService questionSubmitService;
    @Autowired
    QuestionService questionService;

    /**
     * 分页查询(概要)
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = "login")
    public R list(@RequestBody PageVO queryVO) {
        PageUtils page = questionSubmitService.queryPage(queryVO);
        List<QuestionSubmitSimpleVO> questionSubmitSimpleVOList = questionSubmitService.getQuestionSubmitSimpleVOs(page.getList());
        page.setList(questionSubmitSimpleVOList);
        return R.ok().put("data", page);
    }


    /**
     * 获取提交详情（提交记录详情展示页）
     */
    @GetMapping("/detail/vo/{id}")
    public R info(@PathVariable("id") Long id) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
        Question question = questionService.getById(questionSubmit.getQuestionId());
        QuestionSubmitDetailVO questionSubmitDetailVO = QuestionSubmitDetailVO.toQuestionSubmitDetailVO(questionSubmit, question);
        return R.ok().put("data", questionSubmitDetailVO);
    }

    /**
     * 提交作答
     */
    @PostMapping("/do")
    @AuthCheck(mustRole = "login")
    public R doQuestionSubmit(@RequestBody QuestionSubmitDoRequest questionSubmitDoRequest) {
        Long aLong = questionSubmitService.doSubmit(questionSubmitDoRequest);
        return R.ok(aLong);
    }


    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody QuestionSubmit questionSubmit) {
        questionSubmitService.updateById(questionSubmit);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        questionSubmitService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 查询执行结果
     */
    @GetMapping("/result/get")
    public R getExecResult(@RequestParam("sumbitId") Long submitId) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(submitId);
//        if (JudgeStatusEnum.SUCCESS.getValue().equals(questionSubmit.getStatus())
//                || JudgeStatusEnum.FAILURE.getValue().equals(questionSubmit.getStatus())) {
//        }
        return R.ok().put("data", questionSubmit.getJudgeInfo());
    }


}
