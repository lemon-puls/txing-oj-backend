package com.bitdf.txing.oj.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.question.QuestionCommentAddRequest;
import com.bitdf.txing.oj.model.entity.QuestionComment;
import com.bitdf.txing.oj.model.entity.User;
import com.bitdf.txing.oj.model.vo.question.QuestionCommentVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.QuestionCommentService;

import javax.servlet.http.HttpServletRequest;


/**
 * @author lizhiwei
 * @email
 * @date 2023-11-13 21:54:02
 */
@RestController
@RequestMapping("/question/comment")
public class QuestionCommentController {
    @Autowired
    private QuestionCommentService questionCommentService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    UserService userService;

    /**
     * 分页查询
     */
    @PostMapping("/list")
//    @AuthCheck(mustRole = "login")
    public R list(@RequestBody PageVO queryVO, HttpServletRequest request) {
        // TODO 根据题目id查询
        PageUtils page = questionCommentService.queryPage(queryVO);
        // 判断是否已登录
        User loginUser = userService.getLoginUserNoThrow(request);
        List<QuestionCommentVO> list = questionCommentService.getQuestionCommentVOs(page.getList(), loginUser);
        page.setList(list);
        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        QuestionComment questionComment = questionCommentService.getById(id);

        return R.ok().put("questionComment", questionComment);
    }

    /**
     * 发表题目评论
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "login")
    public R save(@RequestBody QuestionCommentAddRequest questionCommentAddRequest) {
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentAddRequest, questionComment);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        questionComment.setUserId(loginUser.getId());
        questionComment.setFavourNum(0);
        boolean save = questionCommentService.save(questionComment);
        ThrowUtils.throwIf(!save, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);

        List<QuestionComment> list = new ArrayList<>();
        list.add(questionComment);
        List<QuestionCommentVO> questionCommentVOs = questionCommentService.getQuestionCommentVOs(list, loginUser);
        return R.ok(questionCommentVOs.get(0));
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody QuestionComment questionComment) {
        questionCommentService.updateById(questionComment);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        questionCommentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 题目评论点赞
     *
     * @param commentId
     * @return
     */
    @GetMapping("/thumb")
    @AuthCheck(mustRole = "login")
    public R thumbQuestionComment(@RequestParam("questionId") Long questionId, @RequestParam("commentId") Long commentId) {
        User user = AuthInterceptor.userThreadLocal.get();
        Long userId = user.getId();
        // 拼接key
        String redisKey = RedisKeyConstant.QUESTION_COMMENT_THUMB + questionId + "-" + commentId;
        BoundSetOperations<String, String> boundSetOps = stringRedisTemplate.boundSetOps(redisKey);
        // 是否已经点赞
        int opsValue = 0;
        if (boundSetOps.isMember(userId.toString())) {
            boundSetOps.remove(userId.toString());
            opsValue = -1;
        } else {
            boundSetOps.add(userId.toString());
            opsValue = 1;
        }
        // 更新数据库
        boolean b = questionCommentService.thumbComment(commentId, opsValue);
        ThrowUtils.throwIf(!b, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        return R.ok();
    }

}
