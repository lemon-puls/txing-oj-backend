package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.vo.question.ChartDataVO;
import com.bitdf.txing.oj.model.vo.question.QuestionSubmitSimpleVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;

import java.util.List;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-13 21:54:02
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    PageUtils queryPage(PageVO queryVO);

    Long doSubmit(QuestionSubmitDoRequest questionSubmit);

    List<QuestionSubmitSimpleVO> getQuestionSubmitSimpleVOs(List<?> list);

    ChartDataVO getChartData(Long userId);
}

