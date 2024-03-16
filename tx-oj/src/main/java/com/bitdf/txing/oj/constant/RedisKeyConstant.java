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

    //  ====================================================聊天相关==========================================================

    /**
     * 房间详情
     */
    public static final String ROOM_INFO = "roomInfo:roomId_%d";
    /**
     * 热门房间
     */
    public static final String HOT_ROOM_ZET = "hotRoom";

    /**
     * RoomFriend and RoomGroup
     *
     * @param key
     * @param objects
     * @return
     */
    public static final String GROUP_INFO_STRING = "groupInfo:roomId_%d";
    public static final String USER_STRING = "user:userid_%d";

    public static final String ONLINE_USERID_ZET = "online";
    public static final String OFFLINE_USERID_ZET = "offline";
    // 用户信息修改时间缓存
    public static final String USER_MODIFY_TIME = "user:modify:uid_%d";

    // 用户相关
    public static final String USER_TOKEN = "user:token:uid_%d";

    /**
     * 比赛相关
     */
    public static final String MATCH_WEEK_RANK = "match:week:rank";

    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }
}
