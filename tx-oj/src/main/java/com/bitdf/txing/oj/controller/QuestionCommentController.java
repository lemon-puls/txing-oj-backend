package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.question.QuestionCommentAddRequest;
import com.bitdf.txing.oj.model.entity.QuestionComment;
import com.bitdf.txing.oj.model.entity.User;
import com.bitdf.txing.oj.model.vo.question.QuestionCommentVO;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("/question/comment")
public class QuestionCommentController {
    @Autowired
    private QuestionCommentService questionCommentService;

    /**
     * 分页查询
     */
    @PostMapping("/list")
    public R list(@RequestBody PageVO queryVO){
        // TODO 根据题目id查询
        PageUtils page = questionCommentService.queryPage(queryVO);
        List<QuestionCommentVO> list = questionCommentService.getQuestionCommentVOs(page.getList());
        page.setList(list);
        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		QuestionComment questionComment = questionCommentService.getById(id);

        return R.ok().put("questionComment", questionComment);
    }

    /**
     * 发表题目评论
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "login")
    public R save(@RequestBody QuestionCommentAddRequest questionCommentAddRequest){
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentAddRequest, questionComment);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        questionComment.setUserId(loginUser.getId());
        questionComment.setFavourNum(0);
        boolean save = questionCommentService.save(questionComment);
        ThrowUtils.throwIf(!save, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody QuestionComment questionComment){
		questionCommentService.updateById(questionComment);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		questionCommentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
