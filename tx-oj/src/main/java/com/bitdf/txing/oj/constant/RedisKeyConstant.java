package com.bitdf.txing.oj.constant;

/**
 * @author Lizhiwei
 * @date 2023/11/19 11:29:08
 * 注释：
 */
public class RedisKeyConstant {

    /**
     * 问题评论点赞
     */
    public static final String QUESTION_COMMENT_THUMB = "oj:question:comment:thumb:";
    /**
     * 文章评论点赞
     */
    public static final String POST_COMMENT_THUMB = "oj:post:comment:thumb:";
    /**
     * 文章内容图片地址记录 以实现自动定期删除无用的图片
     */
    public static final String POST_CONTENT_IMGS_ADD = "oj:post:content:imgs:add";
    public static final String POST_CONTENT_IMGS_UPDATE = "oj:post:content:imgs:update:";
}
