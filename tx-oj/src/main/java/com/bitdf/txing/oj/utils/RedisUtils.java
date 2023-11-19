package com.bitdf.txing.oj.utils;

import com.bitdf.txing.oj.constant.RedisKeyConstant;

/**
 * @author Lizhiwei
 * @date 2023/11/19 14:57:55
 * 注释：
 */
public class RedisUtils {

    /**
     * 获取到 题目评论点赞 Redis key
     *
     * @param questionId
     * @param commentId
     * @return
     */
    public static String getQuestionCommentThumbKey(Long questionId, Long commentId) {
        String key = RedisKeyConstant.QUESTION_COMMENT_THUMB + questionId + "-" + commentId;
        return key;
    }
}
