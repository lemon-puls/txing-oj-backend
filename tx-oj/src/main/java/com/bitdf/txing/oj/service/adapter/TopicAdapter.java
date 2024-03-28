package com.bitdf.txing.oj.service.adapter;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitdf.txing.oj.model.dto.forum.TopicCommentRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicPublishRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.entity.forum.TopicComment;
import com.bitdf.txing.oj.model.entity.forum.TopicFavour;
import com.bitdf.txing.oj.model.entity.forum.TopicThumb;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.forum.TopicCommentVO;
import com.bitdf.txing.oj.model.vo.forum.TopicVO;
import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import com.bitdf.txing.oj.service.TopicFavourService;
import com.bitdf.txing.oj.service.TopicThumbService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TopicAdapter {

    @Autowired
    UserCache userCache;
    @Autowired
    UserService userService;
    @Autowired
    TopicThumbService topicThumbService;
    @Autowired
    TopicFavourService topicFavourService;

    public static Topic buildTopicByTopicAddRequest(TopicPublishRequest request, Long userId) {
        Topic topic = new Topic();
        BeanUtils.copyProperties(request, topic);
        topic.setUserId(userId);
        topic.setCommentNum(0);
        topic.setFavourNum(0);
        topic.setThumbNum(0);
        return topic;
    }

    public List<TopicVO> buildTopicVOsByTopics(List<?> list) {
        List<TopicVO> collect = list.stream().map((item) -> {
            Topic topic = (Topic) item;
            TopicVO topicVO = new TopicVO();
            BeanUtils.copyProperties(topic, topicVO);
            User user = userCache.get(topic.getUserId());
            UserShowVO userShowVO = UserAdapter.buildUserShowVO(user);
            topicVO.setUserShowVO(userShowVO);
            //获取当前登录用户
            User loginUser = userService.getLoginUserPermitNull();
            if (ObjectUtil.isNotNull(loginUser)) {
                Long userId = loginUser.getId();
                // 查询当前用户是否点赞
                TopicThumb topicThumb = TopicThumb.builder()
                        .userId(userId)
                        .topicId(topic.getId())
                        .build();
                QueryWrapper<TopicThumb> wrapper = new QueryWrapper<>(topicThumb);
                TopicThumb thumb = topicThumbService.getOne(wrapper);
                topicVO.setThumb(thumb == null ? false : true);
                // 查询当前用户是否收藏
                TopicFavour topicFavour = TopicFavour.builder()
                        .userId(userId)
                        .topicId(topic.getId())
                        .build();
                QueryWrapper<TopicFavour> wrapper1 = new QueryWrapper<>(topicFavour);
                TopicFavour favour = topicFavourService.getOne(wrapper1);
                topicVO.setFavour(favour == null ? false : true);
            }
            return topicVO;
        }).collect(Collectors.toList());
        return collect;
    }

    public static TopicComment buildTopicCommentByTopicCommentRequest(TopicCommentRequest request, Long userId) {
        TopicComment topicComment = new TopicComment();
        BeanUtils.copyProperties(request, topicComment);
        topicComment.setFavourNum(0);
        topicComment.setUserId(userId);
        return topicComment;
    }

    public static TopicCommentVO buildCommentVOByComment(TopicComment topicComment, List<TopicCommentVO> list,
                                                         User commentUser, User replyUser) {
        TopicCommentVO topicCommentVO = new TopicCommentVO();
        BeanUtils.copyProperties(topicComment, topicCommentVO);
        UserShowVO commentUserShowVO = UserAdapter.buildUserShowVO(commentUser);
        topicCommentVO.setUserShowVO(commentUserShowVO);
        if (replyUser != null) {
            topicCommentVO.setUserName(replyUser.getUserName());
        }
        topicCommentVO.setReplyList(list);
        return topicCommentVO;
    }
}
