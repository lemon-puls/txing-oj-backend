package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.mapper.TopicMapper;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.service.TopicService;
import com.bitdf.txing.oj.utils.CursorUtils;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.stereotype.Service;


@Service("topicService")
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<TopicEntity> page = this.page(
//                new Query<TopicEntity>().getPage(params),
//                new QueryWrapper<TopicEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public PageUtils queryPage(PageVO queryVO) {
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        IPage<Topic> iPage = new Query<Topic>().buildWrapperAndPage(wrapper, queryVO, null);
        IPage<Topic> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

    @Override
    public CursorPageBaseVO<Topic> getTopicPageByCursor(CursorPageBaseRequest pageRequest, String keyWord) {
        CursorPageBaseVO<Topic> cursorPageByMysql = CursorUtils.getCursorPageByMysql(this, pageRequest, wrapper -> {
            wrapper.and((we) -> {
                we.like(Topic::getTitle, keyWord)
                        .or()
                        .like(Topic::getContent, keyWord);
            });
        }, Topic::getCreateTime);
        return cursorPageByMysql;
    }
}