package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.QuestionFavour;
import com.bitdf.txing.oj.utils.page.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-20 19:18:19
 */
public interface QuestionFavourService extends IService<QuestionFavour> {

    PageUtils queryPage(Map<String, Object> params);
}

