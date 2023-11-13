package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.mapper.QuestionSubmitMapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.QuestionSubmitService;


@Service("questionSubmitService")
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Override
    public PageUtils queryPage(PageVO pageVO) {

        QueryWrapper<QuestionSubmit> wrapper = new QueryWrapper<>();

        IPage<QuestionSubmit> iPage = new Query<QuestionSubmit>().buildWrapperAndPage(wrapper, pageVO, null);

        IPage<QuestionSubmit> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

}