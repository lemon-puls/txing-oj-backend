package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.QuestionMapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionFavour;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.bitdf.txing.oj.service.QuestionFavourService;
import com.bitdf.txing.oj.service.QuestionService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.page.FilterVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service("questionService")
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    UserService userService;
    @Autowired
    @Lazy
    QuestionFavourService questionFavourService;

    @Override
    public PageUtils queryPage(PageVO queryVO) {

        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        if (queryVO.getFilter() != null) {
            // 处理tags
            addTagsCondition(wrapper, queryVO, "tags");
            // 处理fixedTags
            addTagsCondition(wrapper, queryVO, "fixedTags");
        }
        // tags、fixedTags需要特殊处理 排除自动拼接条件
        Set<String> excludeFields = new HashSet<>();
        excludeFields.add("tags");
        excludeFields.add("fixed_tags");
        IPage<Question> iPage = new Query<Question>().buildWrapperAndPage(wrapper, queryVO, excludeFields);
        IPage<Question> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

    /**
     * 拼接题目标签查询条件
     *
     * @param wrapper
     * @param queryVO
     * @param targetField
     */
    public void addTagsCondition(QueryWrapper<Question> wrapper, PageVO queryVO, String targetField) {
        List<FilterVO> collect1 = queryVO.getFilter().stream().filter((item) -> {
            return targetField.equals(item.getFieldName());
        }).collect(Collectors.toList());
        if (!collect1.isEmpty()) {
            FilterVO filterVO = collect1.get(0);
            String[] split = filterVO.getValue().split("_");
            if (split != null && split.length != 0 && !"".equals(split[0])) {
                wrapper.lambda().and((wrapper1) -> {
                    for (String tag : split) {
                        wrapper1.or((wrapper2) -> {
                            wrapper2.like(Question::getTags, tag);
                        });
                    }
                });
            }
        }
    }

    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "判题配置过长");
        }
    }

    /**
     * 获取到QuestionVO（集合）
     *
     * @param questionList
     * @param b
     * @return
     */
    @Override
    public List<QuestionVO> getQuestionVOsByQuestions(List<?> questionList, boolean b) {
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map((item -> {
            Question question = (Question) item;
            return question.getUserId();
        })).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(item -> {
            Question question = (Question) item;
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
            if (b) {
                // 设置该用户是否已收藏该题目
                User loginUser = AuthInterceptor.userThreadLocal.get();
                QuestionFavour questionFavour = questionFavourService.getOne(new QueryWrapper<QuestionFavour>().lambda()
                        .eq(QuestionFavour::getQuestionId, question.getId())
                        .eq(QuestionFavour::getUserId, loginUser.getId()));
                questionVO.setIsFavour(questionFavour != null);
            }
            return questionVO;
        }).collect(Collectors.toList());
        return questionVOList;
    }

    /**
     * @return
     */
    @Override
    public List<QuestionVO> getQuestionVOsByIds(List<Long> idList) {
        List<Question> questions = this.listByIds(idList);
        // 不存在
        ThrowUtils.throwIf(questions == null || questions.isEmpty(), TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);

        List<QuestionVO> questionVOS = this.getQuestionVOsByQuestions(questions, false);
        return questionVOS;
    }


    /**
     * 随机抽选任意道题目
     *
     * @return
     */
    @Override
    public List<Question> getQuestionsByRandom(Integer count) {
        // 抽选题目
        List<Question> questions = this.list(new QueryWrapper<Question>().lambda().select(Question::getId));
        List<Question> randomQuestions = selectRandomQuestions(questions, count);
        return randomQuestions;
    }

    /**
     * 抽选题目
     */
    public List<Question> selectRandomQuestions(List<Question> list, int count) {
        List<Question> selectedItems = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count && !list.isEmpty(); i++) {
            int randomIndex = rand.nextInt(list.size());
            selectedItems.add(list.get(randomIndex));
            list.remove(randomIndex); // Ensure no duplicates
        }
        return selectedItems;
    }

}
