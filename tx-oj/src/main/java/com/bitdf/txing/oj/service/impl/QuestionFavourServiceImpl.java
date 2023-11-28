package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.QuestionFavourMapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionFavour;
import com.bitdf.txing.oj.model.entity.User;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.bitdf.txing.oj.service.QuestionService;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.bitdf.txing.oj.service.QuestionFavourService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service("questionFavourService")
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour> implements QuestionFavourService {

    @Autowired
    QuestionService questionService;

    /**
     * 分页查询
     *
     * @param pageVO
     * @return
     */
    @Override
    public PageUtils queryPage(PageVO pageVO) {
        QueryWrapper<QuestionFavour> wrapper = new QueryWrapper<>();
        User loginUser = AuthInterceptor.userThreadLocal.get();
        wrapper.lambda().eq(QuestionFavour::getUserId, loginUser.getId());
        IPage<QuestionFavour> iPage = new Query<QuestionFavour>().buildWrapperAndPage(wrapper, pageVO, null);
        IPage<QuestionFavour> page = this.page(iPage, wrapper);
        PageUtils pageUtils = new PageUtils(page);
        if (!page.getRecords().isEmpty()) {
            List<QuestionVO> questionVOList = getQuestionVOsByFavours(page.getRecords());
            pageUtils.setList(questionVOList);
        }
        return pageUtils;
    }

    /**
     * List<QuestionFavour> ==> List<QuestionVO>
     *
     * @param questionFavourList
     * @return
     */
    public List<QuestionVO> getQuestionVOsByFavours(List<QuestionFavour> questionFavourList) {
        List<Long> questionIds = questionFavourList.stream().map((item) -> {
            return item.getQuestionId();
        }).collect(Collectors.toList());
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(Question::getId, questionIds);
        List<Question> questions = questionService.list(wrapper);
        List<QuestionVO> questionVOList = questionService.getQuestionVOsByQuestions(questions, false);
        return questionVOList;
    }

    /**
     * 收藏 / 取消收藏 题目
     *
     * @param questionId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean favourQuestion(Long questionId) {
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        User loginUser = AuthInterceptor.userThreadLocal.get();
        QueryWrapper<QuestionFavour> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(QuestionFavour::getQuestionId, questionId)
                .eq(QuestionFavour::getUserId, loginUser.getId());
        QuestionFavour favour = this.getOne(wrapper);
        // 每个用户串行操作
        synchronized (String.valueOf(loginUser.getId()).intern()) {
            if (favour == null) {
                // 还未收藏 此时进行收藏
                favour = new QuestionFavour();
                favour.setQuestionId(questionId);
                favour.setUserId(loginUser.getId());
                boolean save = this.save(favour);
                ThrowUtils.throwIf(!save, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
                return true;
            } else {
                // 已收藏 此时进行取消收藏操作
                boolean b = this.removeById(favour.getId());
                ThrowUtils.throwIf(!b, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
                return false;
            }
        }
    }

}