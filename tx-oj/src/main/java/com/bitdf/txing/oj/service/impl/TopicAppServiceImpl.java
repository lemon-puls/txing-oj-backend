package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicCommentRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicPublishRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.entity.forum.TopicComment;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.forum.TopicCommentVO;
import com.bitdf.txing.oj.model.vo.forum.TopicVO;
import com.bitdf.txing.oj.service.TopicAppService;
import com.bitdf.txing.oj.service.TopicCommentService;
import com.bitdf.txing.oj.service.TopicService;
import com.bitdf.txing.oj.service.adapter.TopicAdapter;
import com.bitdf.txing.oj.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicAppServiceImpl implements TopicAppService {

    @Autowired
    TopicService topicService;
    @Autowired
    TopicCommentService topicCommentService;
    @Autowired
    UserCache userCache;
    @Autowired
    TopicAdapter topicAdapter;

    @Override
    public Long addTopic(TopicPublishRequest request, Long userId) {
        Topic topic = TopicAdapter.buildTopicByTopicAddRequest(request, userId);
        boolean save = topicService.save(topic);
        return topic.getId();
    }

    @Override
    public Long commentTopic(TopicCommentRequest request, Long userId) {
        TopicComment topicComment = TopicAdapter.buildTopicCommentByTopicCommentRequest(request, userId);
        topicCommentService.save(topicComment);
        return topicComment.getId();
    }

    @Override
    public List<TopicCommentVO> getCommentsByTopicId(Long topicId) {
        List<TopicComment> list = topicCommentService.getCommentsByTopicId(topicId);
        // 查出一级评论
        List<TopicComment> firstComments = list.stream()
                .filter(comment -> comment.getReplyId() == null || comment.getReplyId() == -1)
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
        // 查出二级评论
        List<TopicComment> secondComments = list.stream()
                .filter(comment -> comment.getReplyId() != null && comment.getReplyId() != -1)
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
        List<TopicCommentVO> commentVOS = firstComments.stream().map(comment -> {
            User replyUser = userCache.get(comment.getUserId());
            // 查出当前评论的所有回复
            List<TopicCommentVO> commentVOList = secondComments.stream().map(item -> {
                        if (comment.getId().equals(item.getReplyId())) {
                            User commentUser = userCache.get(item.getUserId());
                            TopicCommentVO topicCommentVO = TopicAdapter.buildCommentVOByComment(item, null, commentUser, replyUser);
                            return topicCommentVO;
                        } else {
                            return null;
                        }
                    }).filter(item -> ObjectUtil.isNotNull(item))
                    .collect(Collectors.toList());
            // 封装当前评论
            TopicCommentVO topicCommentVO = TopicAdapter.buildCommentVOByComment(comment, commentVOList, replyUser, null);
            return topicCommentVO;
        }).collect(Collectors.toList());
        return commentVOS;
    }

    /**
     * 游标翻页查询 帖子
     *
     * @param pageRequest
     * @return
     */
    @Override
    public CursorPageBaseVO<TopicVO> getTopicPageByCursor(CursorPageBaseRequest pageRequest) {
        CursorPageBaseVO<Topic> cursorPageBaseVO = topicService.getTopicPageByCursor(pageRequest);
        if (cursorPageBaseVO.isEmpty()) {
            return CursorPageBaseVO.empty();
        }
        return CursorPageBaseVO.init(cursorPageBaseVO, topicAdapter.buildTopicVOsByTopics(cursorPageBaseVO.getList()));
    }
}
