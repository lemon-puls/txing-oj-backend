package com.bitdf.txing.oj.service;

import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicCommentRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicPublishRequest;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.forum.TopicCommentVO;
import com.bitdf.txing.oj.model.vo.forum.TopicVO;

import java.util.List;

public interface TopicAppService {
    Long addTopic(TopicPublishRequest request, Long userId);

    Long commentTopic(TopicCommentRequest request, Long userId);

    List<TopicCommentVO> getCommentsByTopicId(Long topicId);

    CursorPageBaseVO<TopicVO> getTopicPageByCursor(CursorPageBaseRequest pageRequest);
}
