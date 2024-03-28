package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2024-03-27 12:59:55
 */
public interface TopicService extends IService<Topic> {
    PageUtils queryPage(PageVO queryVO);

    CursorPageBaseVO<Topic> getTopicPageByCursor(CursorPageBaseRequest pageRequest, String keyWord);

//    PageUtils queryPage(Map<String, Object> params);
}

