package com.bitdf.txing.oj.service.adapter;

import com.bitdf.txing.oj.model.dto.forum.TopicCommentRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicPublishRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.entity.forum.TopicComment;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.forum.TopicCommentVO;
import com.bitdf.txing.oj.model.vo.forum.TopicVO;
import com.bitdf.txing.oj.model.vo.user.UserShowVO;
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
