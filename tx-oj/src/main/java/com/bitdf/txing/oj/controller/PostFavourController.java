package com.bitdf.txing.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.post.PostEsDTO;
import com.bitdf.txing.oj.model.dto.post.PostQueryRequest;
import com.bitdf.txing.oj.model.dto.postfavour.PostFavourAddRequest;
import com.bitdf.txing.oj.model.dto.postfavour.PostFavourQueryRequest;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.service.PostFavourService;
import com.bitdf.txing.oj.service.PostService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@RestController
@RequestMapping("/post_favour")
@Slf4j
public class PostFavourController {

    @Resource
    private PostFavourService postFavourService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param postFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    @AuthCheck(mustRole = "login")
    public R doPostFavour(@RequestBody PostFavourAddRequest postFavourAddRequest,
                          HttpServletRequest request) {
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long postId = postFavourAddRequest.getPostId();
        int result = postFavourService.doPostFavour(postId, loginUser);
        return R.ok(result);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param postQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    @AuthCheck(mustRole = "login")
    public R listMyFavourPostByPage(@RequestBody PostQueryRequest postQueryRequest,
                                    HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        User loginUser = AuthInterceptor.userThreadLocal.get();
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest), loginUser.getId());
        Page<PostEsDTO> postVOPage = postService.getPostVOPage(postPage, request);
        PageUtils pageUtils = new PageUtils(postVOPage);
        return R.ok(pageUtils);
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public R listFavourPostByPage(@RequestBody PostFavourQueryRequest postFavourQueryRequest,
                                  HttpServletRequest request) {
        if (postFavourQueryRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        long current = postFavourQueryRequest.getCurrent();
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postFavourQueryRequest.getPostQueryRequest()), userId);
        Page<PostEsDTO> postVOPage = postService.getPostVOPage(postPage, request);
        PageUtils pageUtils = new PageUtils(postVOPage);
        return R.ok(pageUtils);
    }
}
