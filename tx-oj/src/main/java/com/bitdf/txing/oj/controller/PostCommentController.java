package com.bitdf.txing.oj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.esdao.PostEsDao;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.post.PostCommentAddRequest;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.model.entity.PostComment;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.post.PostCommentVO;
import com.bitdf.txing.oj.service.PostCommentService;
import com.bitdf.txing.oj.service.PostService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.RedisUtils;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/3 1:04:40
 * 注释：
 */
@RestController
@RequestMapping("/post/comment/")
@Slf4j
public class PostCommentController {

    @Autowired
    PostCommentService postCommentService;
    @Autowired
    PostService postService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    UserService userService;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    PostEsDao postEsDao;

    /**
     * 分页查询
     */
    @PostMapping("/list")
//    @AuthCheck(mustRole = "login")
    public R list(@RequestBody PageVO queryVO, HttpServletRequest request) {
        // TODO 根据题目id查询
        PageUtils page = postCommentService.queryPage(queryVO);
        // 判断是否已登录
        User loginUser = userService.getLoginUserNoThrow(request);
        List<PostCommentVO> list = postCommentService.getPostCommentVOs(page.getList(), loginUser);
        page.setList(list);
        return R.ok().put("data", page);
    }


    /**
     * 发表文章评论
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "login")
    @Transactional
    public R save(@RequestBody PostCommentAddRequest postCommentAddRequest) {
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentAddRequest, postComment);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        postComment.setUserId(loginUser.getId());
        postComment.setFavourNum(0);
        boolean save = postCommentService.save(postComment);
        ThrowUtils.throwIf(!save, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        // 更新文章评论数
        boolean update = postService.update(new UpdateWrapper<Post>().lambda().eq(Post::getId, postComment.getPostId())
                .setSql("comment_num = comment_num + 1, update_time = NOW()"));
        ThrowUtils.throwIf(!update, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        // 更新ES
//        UpdateQuery build = UpdateQuery.builder(postComment.getPostId().toString()).withScript("commentNum += 1")
//                .withScriptedUpsert(true).build();
//        elasticsearchRestTemplate.update(build, IndexCoordinates.of("post"));
//        restTemplate.update(build, IndexCoordinates.of("lili_qrworks"));
//        elasticsearchRestTemplate.update(request, RequestOptions.DEFAULT);

        List<PostComment> list = new ArrayList<>();
        list.add(postComment);
        List<PostCommentVO> postCommentVOS = postCommentService.getPostCommentVOs(list, loginUser);
        return R.ok(postCommentVOS.get(0));
    }


    /**
     * 文章评论点赞
     *
     * @param commentId
     * @return
     */
    @GetMapping("/thumb")
    @AuthCheck(mustRole = "login")
    public R thumbQuestionComment(@RequestParam("postId") Long postId, @RequestParam("commentId") Long commentId) {
        User user = AuthInterceptor.userThreadLocal.get();
        Long userId = user.getId();
        // 拼接key
        String redisKey = RedisUtils.getPostCommentThumbKey(postId, commentId);
        BoundSetOperations<String, String> boundSetOps = stringRedisTemplate.boundSetOps(redisKey);
        // 是否已经点赞
        int opsValue = 0;
        if (boundSetOps.isMember(userId.toString())) {
            boundSetOps.remove(userId.toString());
            opsValue = -1;
        } else {
            boundSetOps.add(userId.toString());
            opsValue = 1;
        }
        // 更新数据库
        boolean b = postCommentService.thumbComment(commentId, opsValue);
        ThrowUtils.throwIf(!b, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        return R.ok();
    }

    /**
     * 删除评论
     */
    @AuthCheck(mustRole = "login")
    @GetMapping("/delete")
    public R delete(@RequestParam("postId") Long postId, @RequestParam("commentId") Long commentId) {
        User user = AuthInterceptor.userThreadLocal.get();
        boolean remove = postCommentService.remove(new QueryWrapper<PostComment>().lambda()
                .eq(PostComment::getId, commentId)
                .eq(PostComment::getUserId, user.getId()));
        ThrowUtils.throwIf(!remove, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        boolean update = postService.update(new UpdateWrapper<Post>().lambda()
                .eq(Post::getId, postId).setSql("comment_num = comment_num - 1, update_time = NOW()"));
        ThrowUtils.throwIf(!update, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        return R.ok();
    }

}
