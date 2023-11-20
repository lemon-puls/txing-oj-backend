package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.mapper.QuestionFavourMapper;
import com.bitdf.txing.oj.model.entity.QuestionFavour;
import com.bitdf.txing.oj.utils.page.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.bitdf.txing.oj.service.QuestionFavourService;


@Service("questionFavourService")
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour> implements QuestionFavourService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        return null;
    }

}