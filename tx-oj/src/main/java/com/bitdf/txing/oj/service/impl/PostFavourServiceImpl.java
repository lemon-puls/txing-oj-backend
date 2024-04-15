package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.job.cycle.IncSyncPostToEs;
import com.bitdf.txing.oj.mapper.PostFavourMapper;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.model.entity.PostFavour;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.CheckStatusEnum;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.service.PostFavourService;
import com.bitdf.txing.oj.service.PostService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 帖子收藏服务实现
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
        implements PostFavourService {

    @Resource
    private PostService postService;
    @Autowired
    IncSyncPostToEs incSyncPostToEs;

    /**
     * 帖子收藏
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostFavour(long postId, User loginUser) {
        // 判断是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        // 是否已帖子收藏
        long userId = loginUser.getId();
        // 每个用户串行帖子收藏
        // 锁必须要包裹住事务方法
        PostFavourService postFavourService = (PostFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postFavourService.doPostFavourInner(userId, postId);
        }
    }

    @Override
    public Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        // 只查出审核通过的
        ((QueryWrapper<Post>) queryWrapper).lambda().eq(Post::getStatus, CheckStatusEnum.ACCEPTED.getCode());
        return baseMapper.listFavourPostByPage(page, queryWrapper, favourUserId);
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
    public int doPostFavourInner(long userId, long postId) {
        PostFavour postFavour = new PostFavour();
        postFavour.setUserId(userId);
        postFavour.setPostId(postId);
        QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>(postFavour);
        PostFavour oldPostFavour = this.getOne(postFavourQueryWrapper);
        boolean result;
        // 已收藏
        if (oldPostFavour != null) {
            result = this.remove(postFavourQueryWrapper);
            if (result) {
                // 帖子收藏数 - 1
//                result = postService.update()
//                        .eq("id", postId)
//                        .gt("favour_num", 0)
//                        .setSql("favour_num = favour_num - 1")
//                        .update();
                result = postService.update(new UpdateWrapper<Post>().lambda()
                        .eq(Post::getId, postId)
                        .gt(Post::getFavourNum, 0)
                        .setSql("favour_num = favour_num - 1, update_time = NOW()"));
//                Post post = postService.getById(postId);
//                post.setFavourNum(post.getFavourNum() - 1);
//                postService.updateById(post);
                incSyncPostToEs.run();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        } else {
            // 未帖子收藏
            result = this.save(postFavour);
            if (result) {
                // 帖子收藏数 + 1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("favour_num = favour_num + 1, update_time = NOW()")
                        .update();
                incSyncPostToEs.run();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION);
            }
        }
    }

}




