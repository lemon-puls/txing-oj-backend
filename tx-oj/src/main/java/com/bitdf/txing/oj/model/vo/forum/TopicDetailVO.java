package com.bitdf.txing.oj.model.vo.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDetailVO {
    private TopicVO topicVO;

    private List<TopicCommentVO> commentVOS;
}
