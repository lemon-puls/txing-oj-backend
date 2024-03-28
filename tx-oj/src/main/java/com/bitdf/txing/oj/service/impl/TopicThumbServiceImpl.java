package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.mapper.TopicThumbMapper;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.entity.forum.TopicThumb;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.service.TopicService;
import com.bitdf.txing.oj.service.TopicThumbService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("topicThumbService")
public class TopicThumbServiceImpl extends ServiceImpl<TopicThumbMapper, TopicThumb> implements TopicThumbService {

    @Autowired
    TopicService topicService;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<TopicThumbEntity> page = this.page(
//                new Query<TopicThumbEntity>().getPage(params),
//                new QueryWrapper<TopicThumbEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public int doPostThumb(long topicId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Topic topic = topicService.getById(topicId);
        if (topic == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        TopicThumbService topicThumbService = (TopicThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return topicThumbService.doPostThumbInner(userId, topicId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, long topicId) {
        TopicThumb topicThumb = new TopicThumb();
        topicThumb.setUserId(userId);
        topicThumb.setTopicId(topicId);
        QueryWrapper<TopicThumb> wrapper = new QueryWrapper<>(topicThumb);
        TopicThumb oldTopicThumb = this.getOne(wrapper);
        boolean result;
        // 已点赞
        if (oldTopicThumb != null) {
            result = this.remove(wrapper);
            if (result) {
                // 点赞数 - 1
                result = topicService.update()
                        .eq("id", topicId)
                        .gt("thumb_num", 0)
                        .setSql("thumb_num = thumb_num - 1, update_time = NOW()")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        } else {
            // 未点赞
            result = this.save(topicThumb);
            if (result) {
                // 点赞数 + 1
                result = topicService.update()
                        .eq("id", topicId)
                        .setSql("thumb_num = thumb_num + 1, update_time = NOW()")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        }
    }

}