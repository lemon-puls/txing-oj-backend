package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.mapper.PostThumbMapper;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.model.entity.PostThumb;
import com.bitdf.txing.oj.model.entity.User;
import com.bitdf.txing.oj.service.PostService;
import com.bitdf.txing.oj.service.PostThumbService;
import javax.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 帖子点赞服务实现
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostThumb(long postId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        PostThumbService postThumbService = (PostThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postThumbService.doPostThumbInner(userId, postId);
        }
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> thumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(thumbQueryWrapper);
        boolean result;
        // 已点赞
        if (oldPostThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = postService.update()
                        .eq("id", postId)
                        .gt("thumb_num", 0)
                        .setSql("thumb_num = thumb_num - 1, update_time = NOW()")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 点赞数 + 1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("thumb_num = thumb_num + 1, update_time = NOW()")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        }
    }

}




