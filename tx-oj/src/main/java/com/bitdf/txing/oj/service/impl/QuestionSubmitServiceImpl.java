package com.bitdf.txing.oj.service.impl;

import cn.hutool.json.JSONUtil;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.model.enume.LanguageEnum;
import com.bitdf.txing.oj.model.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.JudgeService;
import com.bitdf.txing.oj.mapper.QuestionSubmitMapper;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.question.QuestionSubmitSimpleVO;
import com.bitdf.txing.oj.service.QuestionService;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.QuestionSubmitService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.stream.Collectors;


@Service("questionSubmitService")
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Autowired
    QuestionService questionService;
    @Autowired
    JudgeService judgeService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(PageVO pageVO) {

        QueryWrapper<QuestionSubmit> wrapper = new QueryWrapper<>();

        IPage<QuestionSubmit> iPage = new Query<QuestionSubmit>().buildWrapperAndPage(wrapper, pageVO, null);

        IPage<QuestionSubmit> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

    /**
     * 提交代码
     */
    @Override
    public Long doSubmit(QuestionSubmitDoRequest questionSubmitDoRequest) {
        // TODO 防止重复提交题目
        // 获取当前登录用户信息
        User loginUser = AuthInterceptor.userThreadLocal.get();
        // 校验编程语言是否合法
        String language = questionSubmitDoRequest.getLanguage();
        LanguageEnum languageEnum = LanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        // 判断目标题目是否存在
        Question question = questionService.getById(questionSubmitDoRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        // 提交到数据库
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setCode(questionSubmitDoRequest.getCode());
        questionSubmit.setLanguage(language);
        questionSubmit.setQuestionId(questionSubmitDoRequest.getQuestionId());
        questionSubmit.setUserId(loginUser.getId());
        // 设置初始状态：等待中
        questionSubmit.setStatus(JudgeStatusEnum.WAITTING.getValue());
        questionSubmit.setJudgeInfo("{}");
        this.save(questionSubmit);
//        // 执行判题服务
//        CompletableFuture.runAsync(() -> {
//            AuthInterceptor.userThreadLocal.set(loginUser);
//            judgeService.doJudge(questionSubmit.getId());
//        });
        rabbitTemplate.convertAndSend(MyMqConfig.JUDGE_EXCHANGE, MyMqConfig.WAITTING_JUDGE_ROUTINGKEY,
                questionSubmit.getId(), new CorrelationData(questionSubmit.getId().toString()));
        return questionSubmit.getId();
    }

    /**
     * List<QuestionSubmit> ==> List<QuestionSubmitSimpleVO>
     *
     * @param list
     * @return
     */
    @Override
    public List<QuestionSubmitSimpleVO> getQuestionSubmitSimpleVOs(List<?> list) {
        List<QuestionSubmitSimpleVO> collect = list.stream().map((item) -> {
            QuestionSubmit questionSubmit = (QuestionSubmit) item;
            QuestionSubmitSimpleVO questionSubmitSimpleVO = new QuestionSubmitSimpleVO();
            BeanUtils.copyProperties(questionSubmit, questionSubmitSimpleVO);
            //设置状态
            questionSubmitSimpleVO.setStatus(JudgeStatusEnum.getByValue(questionSubmit.getStatus()).getText());
            JudgeInfo judgeInfo = JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class);
            questionSubmitSimpleVO.setMemory(judgeInfo.getMemory());
            questionSubmitSimpleVO.setTimes(judgeInfo.getTime());
            questionSubmitSimpleVO.setResult(judgeInfo.getMessage());
//            if (questionSubmit.getCreateTime() != null) {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
//                String format = simpleDateFormat.format(questionSubmit.getCreateTime());
//                questionSubmitSimpleVO.setCreateTime(format);
//            } else {
//                questionSubmitSimpleVO.setCreateTime("");
//            }
            // 查询题目
            Question question = questionService.getById(questionSubmit.getQuestionId());
            questionSubmitSimpleVO.setTitle(question.getTitle());
            return questionSubmitSimpleVO;
        }).collect(Collectors.toList());
        return collect;
    }

}
