package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.PostCommentMapper;
import com.bitdf.txing.oj.mapper.PostFavourMapper;
import com.bitdf.txing.oj.model.entity.*;
import com.bitdf.txing.oj.model.entity.PostComment;
import com.bitdf.txing.oj.model.vo.post.PostCommentVO;
import com.bitdf.txing.oj.service.PostCommentService;
import com.bitdf.txing.oj.service.PostFavourService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.RedisUtils;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/3 1:03:34
 * 注释：
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
        implements PostCommentService {

    @Autowired
    UserService userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 分页查询
     * @param queryVO
     * @return
     */
    @Override
    public PageUtils queryPage(PageVO queryVO) {
        QueryWrapper<PostComment> wrapper = new QueryWrapper<>();

        IPage<PostComment> iPage = new Query<PostComment>().buildWrapperAndPage(wrapper, queryVO, null);

        IPage<PostComment> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

    /**
     * 把PostComment集合转换为PostCommentVO集合
     *
     * @param list
     * @param loginUser
     * @return
     */
    @Override
    public List<PostCommentVO> getPostCommentVOs(List<?> list, User loginUser) {
        List<PostCommentVO> collect = list.stream().map((item) -> {
            PostComment postComment = (PostComment) item;
            PostCommentVO postCommentVO = new PostCommentVO();
            BeanUtils.copyProperties(postComment, postCommentVO);
            // 获取对应的用户
            User user = userService.getById(postCommentVO.getUserId());
            ThrowUtils.throwIf(user == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
            // 设置用户名
            postCommentVO.setUserName(user.getUserName());
            // 设置头像
            postCommentVO.setUserAvatar(user.getUserAvatar());
            // 判断是否点赞
            if (loginUser != null) {
                BoundSetOperations<String, String> boundSetOps = stringRedisTemplate
                        .boundSetOps(RedisUtils.getPostCommentThumbKey(postComment.getPostId(), postComment.getId()));
                Boolean isMember = boundSetOps.isMember(loginUser.getId().toString());
                postCommentVO.setIsFavour(isMember);
            } else {
                postCommentVO.setIsFavour(false);
            }
            return postCommentVO;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 更新文章评论点赞数
     *
     * @param commentId
     * @param opsValue
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean thumbComment(Long commentId, int opsValue) {
        UpdateWrapper<PostComment> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(PostComment::getId, commentId)
                .gt(opsValue == -1, PostComment::getFavourNum, 0)
                .setSql(opsValue == -1, "favour_num = favour_num - 1")
                .setSql(opsValue == 1, "favour_num = favour_num + 1");
        boolean update = this.update(wrapper);
        return update;
    }
}
