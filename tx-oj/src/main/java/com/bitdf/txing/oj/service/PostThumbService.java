package com.bitdf.txing.oj.service;

import com.bitdf.txing.oj.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.user.User;

/**
 * 帖子点赞服务
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);
}
