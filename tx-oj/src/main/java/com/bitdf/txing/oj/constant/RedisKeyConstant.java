package com.bitdf.txing.oj.constant;

/**
 * @author Lizhiwei
 * @date 2023/11/19 11:29:08
 * 注释：
 */
public class RedisKeyConstant {

    public static final String BASE_KEY = "oj:";

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
    /**
     * 房间详情
     */
    public static final String ROOM_INFO = "roomInfo:roomId_%d";


    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }
}
