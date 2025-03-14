package com.bitdf.txing.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.common.DeleteRequest;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.esdao.PostEsDao;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.job.cycle.IncSyncPostToEs;
import com.bitdf.txing.oj.manager.CosManager;
import com.bitdf.txing.oj.model.dto.post.*;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.post.PostUpdateVO;
import com.bitdf.txing.oj.service.PostService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.CustomStringUtils;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 帖子接口
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();
    @Autowired
    IncSyncPostToEs incSyncPostToEs;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    PostEsDao postEsDao;
    @Autowired
    CosManager cosManager;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    // region 增删改查

    /**
     * 创建
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "login")
    public R addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
//        List<String> tags = postAddRequest.getTags();
//        if (tags != null) {
//            post.setTags(GSON.toJson(tags));
//        }
        postService.validPost(post, true);
//        User loginUser = userService.getLoginUser(request);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        post.setUserId(loginUser.getId());
        post.setFavourNum(0);
        post.setThumbNum(0);
        post.setCommentNum(0);
        post.setIsDelete(0);
        // 保存到mysql
        boolean result = postService.save(post);
        ThrowUtils.throwIf(!result, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        List<Post> list = new ArrayList<>();
        list.add(post);
        List<PostEsDTO> postEsDTOByPosts = postService.getPostEsDTOByPosts(list);
        // 保存到es
        postEsDao.save(postEsDTOByPosts.get(0));
        long newPostId = post.getId();
        // 从Redis中移除文章中实际用到了的图片url
        List<String> collect = CustomStringUtils.getImgUrlsFromMd(post.getContent(), "https", true);
        BoundHashOperations<String, String, String> imgsHashOps = stringRedisTemplate.boundHashOps(RedisKeyConstant.POST_CONTENT_IMGS_ADD);
        if (!collect.isEmpty()) {
            Long delete = imgsHashOps.delete(collect.toArray());
        }
        Map<String, String> entries = imgsHashOps.entries();
        BoundHashOperations<String, String, String> updateImgsHashOps =
                stringRedisTemplate.boundHashOps(RedisKeyConstant.POST_CONTENT_IMGS_UPDATE + post.getId());
        updateImgsHashOps.putAll(entries);
        return R.ok(newPostId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = "login")
    public R deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
//        User user = userService.getLoginUser(request);
        User user = AuthInterceptor.userThreadLocal.get();
        long id = deleteRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(TxCodeEnume.COMMON_NOT_PERM_EXCEPTION);
        }
        boolean b = postService.removeById(id);
        if (b) {
//            CompletableFuture.runAsync(() -> {
//                String delete = elasticsearchRestTemplate.delete(String.valueOf(id), PostEsDTO.class);
//            });
            String delete = elasticsearchRestTemplate.delete(String.valueOf(id), PostEsDTO.class);
            // 获取所有要删除的图片
            String prefix = "myqcloud.com/";
            List<String> collect = CustomStringUtils.getImgUrlsFromMd(oldPost.getContent(), prefix, false);
            collect.add(oldPost.getCoverImg().substring(oldPost.getCoverImg().indexOf(prefix) + prefix.length()));
            // 创建 DeleteObjectsRequest 对象
            cosManager.deleteOjects(collect);
        }
        return R.ok(b);
    }

    /**
     * 更新（仅管理员）
     * @param postUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = "login")
    public R updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
//        List<String> tags = postUpdateRequest.getTags();
//        if (tags != null) {
//            post.setTags(GSON.toJson(tags));
//        }
        // 参数校验
        postService.validPost(post, false);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        // 判断是否本人操作
        User loginUser = AuthInterceptor.userThreadLocal.get();
        ThrowUtils.throwIf(!loginUser.getId().equals(oldPost.getUserId()), TxCodeEnume.COMMON_NOT_PERM_EXCEPTION);
        // 更新mysql
        boolean result = postService.updateById(post);
        // 更新es
        incSyncPostToEs.run();
        return R.ok(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public R getPostVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        }
        return R.ok(postService.getPostVO(post, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public R listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                              HttpServletRequest request) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return R.ok(postService.getPostVOPage(postPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @AuthCheck(mustRole = "login")
    public R listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
//        User loginUser = userService.getLoginUser(request);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        postQueryRequest.setUserId(loginUser.getId());
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return R.ok(postService.getPostVOPage(postPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public R searchPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                HttpServletRequest request) {
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        Page<PostEsDTO> postPage = postService.searchFromEs(postQueryRequest);
        PageUtils pageUtils = new PageUtils(postPage);
//        return R.ok(postService.getPostVOPage(postPage, request));
        return R.ok(pageUtils);
    }

    /**
     * 编辑（用户）
     *
     * @param postEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public R editPost(@RequestBody PostEditRequest postEditRequest, HttpServletRequest request) {
        if (postEditRequest == null || postEditRequest.getId() <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        User loginUser = userService.getLoginUser(request);
        long id = postEditRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        // 仅本人或管理员可编辑
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(TxCodeEnume.COMMON_NOT_PERM_EXCEPTION);
        }
        boolean result = postService.updateById(post);
        return R.ok(result);
    }

    /**
     * 更新时获取文章原数据
     */
    @GetMapping("/update/vo/get")
    public R getPostUpdateVO(@RequestParam("id") Long id) {
        Post post = postService.getById(id);
        ThrowUtils.throwIf(post == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        PostUpdateVO postUpdateVO = new PostUpdateVO();
        BeanUtils.copyProperties(post, postUpdateVO);
        // 把文章中的图片地址存到Redis(为了后续方便删除多余的图片)
        BoundHashOperations<String, String, String> imgsHashOps =
                stringRedisTemplate.boundHashOps(RedisKeyConstant.POST_CONTENT_IMGS_UPDATE + post.getId());
        // 从文章中获取图片url集合
        List<String> collect = CustomStringUtils.getImgUrlsFromMd(post.getContent(), "https", true);
        Map<String, String> map = collect.stream().collect(Collectors.toMap(k -> k, v -> System.currentTimeMillis() + ""));
        imgsHashOps.putAll(map);
        return R.ok(postUpdateVO);
    }

}
