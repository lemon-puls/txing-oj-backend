package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.mapper.QuestionMapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.QuestionService;


@Service("questionService")
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    public PageUtils queryPage(PageVO queryVO) {

        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        IPage<Question> iPage = new Query<Question>().buildWrapperAndPage(wrapper, queryVO, null);

        IPage<Question> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

}