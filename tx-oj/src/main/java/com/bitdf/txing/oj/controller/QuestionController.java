package com.bitdf.txing.oj.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.constant.UserConstant;
import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.question.*;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.User;
import com.bitdf.txing.oj.model.vo.question.QuestionManageVO;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.QuestionService;


/**
 * @author lizhiwei
 * @email
 * @date 2023-11-13 21:54:02
 */
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;

    /**
     * 分页查询(题目浏览页)
     */
    @PostMapping("/list")
    public R list(@RequestBody PageVO queryVO) {
        PageUtils page = questionService.queryPage(queryVO);
        if (!page.getList().isEmpty()) {
            List<QuestionVO> questionVOList = questionService.getQuestionVOsByQuestions(page.getList());
            page.setList(questionVOList);
        }
        return R.ok().put("data", page);
    }

    /**
     * 分页查询（题目管理页）
     */
    @PostMapping("/manager/list")
    public R listForManager(@RequestBody PageVO queryVO) {
        PageUtils page = questionService.queryPage(queryVO);
        List<QuestionManageVO> collect = page.getList().stream().map((item) -> {
            QuestionManageVO manageVO = QuestionManageVO.objToVo((Question) item);
            return manageVO;
        }).collect(Collectors.toList());
        page.setList(collect);
        return R.ok().put("data", page);
    }


    /**
     * 根据 id 获取
     */
    @GetMapping("/info/{id}")
    @AuthCheck(mustRole = "login")
    public R info(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        User loginUser = AuthInterceptor.userThreadLocal.get();
        // 不是本人或管理员，不能直接获取所有信息
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(TxCodeEnume.COMMON_NOT_PERM_EXCEPTION);
        }
        return R.ok(question);
    }

    /**
     * 通过id获取QuestionVO
     *
     * @return
     */
    @GetMapping("/vo/get/id")
    public R getQuestionVoById(@RequestParam("id") Long id) {
        Question question = questionService.getById(id);
        // 不存在
        ThrowUtils.throwIf(question == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        List<Question> questions = new ArrayList<>();
        questions.add(question);
        List<QuestionVO> questionVOS = questionService.getQuestionVOsByQuestions(questions);
        return R.ok(questionVOS.get(0));
    }

    /**
     * 添加题目
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "login")
    public R addQuestion(@RequestBody QuestionAddRequest questionAddRequest) {
        if (questionAddRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);

        question.setSubmitNum(0);
        question.setAcceptedNum(0);

        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        long newQuestionId = question.getId();
        return R.ok(newQuestionId);
    }


    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        boolean result = questionService.updateById(question);
        return R.ok(result);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = "login")
    public R deleteQuestionByIds(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        User user = AuthInterceptor.userThreadLocal.get();
        boolean b;
        if (UserConstant.ADMIN_ROLE.equals(user.getUserRole())) {
            b = questionService.removeByIds(Arrays.asList(ids));
        } else {
            QueryWrapper<Question> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(Question::getId, Arrays.asList(ids))
                    .eq(Question::getUserId, user.getId());
            b = questionService.remove(wrapper);
        }
        return R.ok(b);
    }

    /**
     * temp
     */
    @PostMapping("/temp1")
    public R save(@RequestBody Question question) {
        questionService.save(question);

        return R.ok();
    }

    /**
     * temp
     */
    @PostMapping("/temp2")
    public R save1(@RequestBody QuestionVO questionVO) {

        return R.ok();
    }

    /**
     * 题目收藏与取消
     *
     * @param questionId
     * @return
     */
    @GetMapping("/favour")
    @AuthCheck(mustRole = "login")
    public R favourQuestion(@RequestParam("questionId") Long questionId) {
        return R.ok();
    }
}
