package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.mapper.TopicFavourMapper;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.entity.forum.TopicFavour;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.service.TopicFavourService;
import com.bitdf.txing.oj.service.TopicService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("topicFavourService")
public class TopicFavourServiceImpl extends ServiceImpl<TopicFavourMapper, TopicFavour> implements TopicFavourService {


    @Autowired
    TopicService topicService;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<TopicFavourEntity> page = this.page(
//                new Query<TopicFavourEntity>().getPage(params),
//                new QueryWrapper<TopicFavourEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public int doFavour(long topicId, User loginUser) {
        // 判断是否存在
        Topic topic = topicService.getById(topicId);
        if (topic == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        // 是否已帖子收藏
        long userId = loginUser.getId();
        // 每个用户串行帖子收藏
        // 锁必须要包裹住事务方法
        TopicFavourService topicFavourService = (TopicFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return topicFavourService.doFavourInner(userId, topicId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doFavourInner(long userId, long topicId) {
        TopicFavour topicFavour = new TopicFavour();
        topicFavour.setTopicId(topicId);
        topicFavour.setUserId(userId);
        QueryWrapper<TopicFavour> wrapper = new QueryWrapper<>(topicFavour);
        TopicFavour oldTopicFavour = this.getOne(wrapper);
        boolean result;
        // 已收藏
        if (oldTopicFavour != null) {
            result = this.remove(wrapper);
            if (result) {
                // 帖子收藏数 - 1
                result = topicService.update(new UpdateWrapper<Topic>().lambda()
                        .eq(Topic::getId, topicId)
                        .gt(Topic::getFavourNum, 0)
                        .setSql("favour_num = favour_num - 1, update_time = NOW()"));
                return result ? -1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        } else {
            // 未帖子收藏
            result = this.save(topicFavour);
            if (result) {
                // 帖子收藏数 + 1
                result = topicService.update()
                        .eq("id", topicId)
                        .setSql("favour_num = favour_num + 1, update_time = NOW()")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        }
    }

}