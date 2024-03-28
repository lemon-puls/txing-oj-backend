package com.bitdf.txing.oj.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.forum.ForumCursorPageRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    @Transactional(rollbackFor = Exception.class)
    public Long commentTopic(TopicCommentRequest request, Long userId) {
        TopicComment topicComment = TopicAdapter.buildTopicCommentByTopicCommentRequest(request, userId);
        topicCommentService.save(topicComment);
        // 更新该话题评论数
        UpdateWrapper<Topic> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .eq(Topic::getId, request.getTopicId())
                .setSql("comment_num = comment_num + 1");
        boolean update = topicService.update(wrapper);
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

            List<TopicCommentVO> replyCommentVOs = getReplyCommentDfs(secondComments, comment);
            // 使用自定义的Comparator对List进行排序
            Collections.sort(replyCommentVOs, (a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
            // 封装当前评论
            TopicCommentVO topicCommentVO = TopicAdapter.buildCommentVOByComment(comment, replyCommentVOs, replyUser, null);
            return topicCommentVO;
        }).collect(Collectors.toList());
        return commentVOS;
    }


    public List<TopicCommentVO> getReplyCommentDfs(List<TopicComment> comments, TopicComment parentComment) {
        User replyUser = userCache.get(parentComment.getUserId());
        // 查出当前评论的所有回复
        List<List<TopicCommentVO>> collect = comments.stream().map(item -> {
                    if (parentComment.getId().equals(item.getReplyId())) {
                        User commentUser = userCache.get(item.getUserId());
                        List<TopicCommentVO> replyCommentVos = getReplyCommentDfs(comments, item);
                        TopicCommentVO topicCommentVO = TopicAdapter.buildCommentVOByComment(item, null, commentUser, replyUser);
                        replyCommentVos.add(topicCommentVO);
                        return replyCommentVos;
                    } else {
                        return null;
                    }
                }).filter(item1 -> ObjectUtil.isNotNull(item1))
                .collect(Collectors.toList());
        if (collect == null || collect.isEmpty()) {
            return new ArrayList<TopicCommentVO>();
        }
        // 创建一个新的 List 用于存放所有元素
        List<TopicCommentVO> commentVOList = new ArrayList<>();
        // 遍历 collect1 中的每个 List
        for (List<TopicCommentVO> list1 : collect) {
            // 遍历每个内部 List 中的元素，并将它们添加到 allElements 中
            for (TopicCommentVO element : list1) {
                commentVOList.add(element);
            }
        }
        return commentVOList;
    }

    /**
     * 游标翻页查询 帖子
     *
     * @param pageRequest
     * @return
     */
    @Override
    public CursorPageBaseVO<TopicVO> getTopicPageByCursor(ForumCursorPageRequest pageRequest) {
        CursorPageBaseVO<Topic> cursorPageBaseVO = topicService.getTopicPageByCursor(pageRequest, pageRequest.getKeyWord());
        if (cursorPageBaseVO.isEmpty()) {
            return CursorPageBaseVO.empty();
        }
        return CursorPageBaseVO.init(cursorPageBaseVO, topicAdapter.buildTopicVOsByTopics(cursorPageBaseVO.getList()));
    }

    /**
     * 删除评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        TopicComment comment = topicCommentService.getById(commentId);
        ThrowUtils.throwIf(!comment.getUserId().equals(userId), "无权限进行该操作");
        boolean b = topicCommentService.removeById(commentId);
        // 更新该话题评论数
        UpdateWrapper<Topic> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .eq(Topic::getId, comment.getTopicId())
                .setSql("comment_num = comment_num - 1");
        boolean update = topicService.update(wrapper);
    }
}
