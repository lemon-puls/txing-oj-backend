package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.QuestionFavour;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-20 19:18:19
 */
public interface QuestionFavourService extends IService<QuestionFavour> {

    PageUtils queryPage(PageVO params);

    Boolean favourQuestion(Long questionId);
}

