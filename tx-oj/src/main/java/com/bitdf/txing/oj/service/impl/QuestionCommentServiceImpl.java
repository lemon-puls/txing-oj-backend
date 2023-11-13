package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.mapper.QuestionCommentMapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionComment;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.QuestionCommentService;


@Service("questionCommentService")
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment> implements QuestionCommentService {

    @Override
    public PageUtils queryPage(PageVO queryVO) {

        QueryWrapper<QuestionComment> wrapper = new QueryWrapper<>();

        IPage<QuestionComment> iPage = new Query<QuestionComment>().buildWrapperAndPage(wrapper, queryVO, null);

        IPage<QuestionComment> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

}