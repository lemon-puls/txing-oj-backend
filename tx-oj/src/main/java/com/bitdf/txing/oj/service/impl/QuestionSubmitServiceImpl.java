package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.judge.JudgeService;
import com.bitdf.txing.oj.mapper.QuestionSubmitMapper;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.JudgeStatusEnum;
import com.bitdf.txing.oj.model.enume.LanguageEnum;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.question.ChartDataVO;
import com.bitdf.txing.oj.model.vo.question.QuestionSubmitSimpleVO;
import com.bitdf.txing.oj.service.QuestionService;
import com.bitdf.txing.oj.service.QuestionSubmitService;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Override
    public ChartDataVO getChartData(Long userId) {
        List<Integer> questionCounts = new ArrayList<>();
        List<Integer> submitCounts = new ArrayList<>();
        List<Integer> acCounts = new ArrayList<>();
        List<Integer> acRates = new ArrayList<>();
        // 获取过去十天Date
        List<Date> dateList = getTenDaysDates();
        Date startTime = dateList.get(0);
        Date endTime = new Date();
        // 获取过去十天所有的提交记录
        QueryWrapper<QuestionSubmit> wrapper = new QueryWrapper<>();
        wrapper.lambda().ge(QuestionSubmit::getCreateTime, startTime)
                .le(QuestionSubmit::getCreateTime, endTime)
                .eq(QuestionSubmit::getUserId, userId);
        List<QuestionSubmit> submits = this.list(wrapper);
        // 根据日期对提交记录进行分组
        Map<Date, List<QuestionSubmit>> groupedSubmits = submits.stream()
                .collect(Collectors.groupingBy(submit -> {
                    // 使用创建时间的年月日作为分组依据
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(submit.getCreateTime());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    return calendar.getTime();
                }));
        // 使用TreeMap对键进行排序
        Map<Date, List<QuestionSubmit>> sortedSubmits = new TreeMap<>(groupedSubmits);
        // 遍历每天的提交记录分组进行处理
//        for (List<QuestionSubmit> submitList : sortedSubmits.values()) {
        for (Date date : dateList) {
            List<QuestionSubmit> submitList = groupedSubmits.get(date);
            if (ObjectUtil.isNull(submitList) || submitList.isEmpty()) {
                questionCounts.add(0);
                submitCounts.add(0);
                acCounts.add(0);
                acRates.add(0);
                continue;
            }
            Set<Long> questionIdSet = new HashSet<>();
            int submitCount = 0;
            int acCount = 0;
            int acRate = 0;
            for (QuestionSubmit submit : submitList) {
                submitCount++;
                if (submit.getExceedPercent() != null && submit.getExceedPercent() >= 0) {
                    // ac
                    acCount++;
                    questionIdSet.add(submit.getQuestionId());
                }
            }
            int questionCount = questionIdSet.size();
            double devideVal = (double) acCount / submitCount * 100;
            acRate = (int) Math.ceil(devideVal);
            // 保存当天数据
            questionCounts.add(questionCount);
            submitCounts.add(submitCount);
            acCounts.add(acCount);
            acRates.add(acRate);
        }
        return new ChartDataVO(dateList, questionCounts, submitCounts, acCounts, acRates);
    }

    public List<Date> getTenDaysDates() {

        // 创建一个 List 用于存储过去十天零点的 Date 对象
        List<Date> tenDaysMidnights = new ArrayList<>();

        // 获取当前时间的 Calendar 实例
        Calendar calendar = Calendar.getInstance();

        // 获取当前时间的 Date 对象
        Date currentDate = calendar.getTime();

        // 将当前时间调整为凌晨
        calendar.add(Calendar.DAY_OF_MONTH, -9);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 逐一获取过去十天的零点时间并存储到 List 中
        for (int i = 0; i < 10; i++) {
            // 添加当前零点时间到 List 中
            tenDaysMidnights.add(calendar.getTime());

            // 将时间向前调整一天
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        // 输出结果
        System.out.println("当前时间: " + currentDate);
        System.out.println("过去十天零点的 Date 集合: ");
        for (Date date : tenDaysMidnights) {
            System.out.println(date);
        }
        return tenDaysMidnights;
    }
}
