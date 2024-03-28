package com.bitdf.txing.oj.controller.forum;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.model.dto.forum.ForumCursorPageRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicCommentRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicPublishRequest;
import com.bitdf.txing.oj.model.dto.postfavour.PostFavourAddRequest;
import com.bitdf.txing.oj.model.dto.question.postthumb.PostThumbAddRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.forum.TopicCommentVO;
import com.bitdf.txing.oj.model.vo.forum.TopicDetailVO;
import com.bitdf.txing.oj.model.vo.forum.TopicVO;
import com.bitdf.txing.oj.service.TopicAppService;
import com.bitdf.txing.oj.service.TopicFavourService;
import com.bitdf.txing.oj.service.TopicService;
import com.bitdf.txing.oj.service.TopicThumbService;
import com.bitdf.txing.oj.service.adapter.TopicAdapter;
import com.bitdf.txing.oj.utils.R;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/forum/topic")
public class TopicAppController {

    @Autowired
    TopicAppService topicAppService;
    @Autowired
    TopicService topicService;
    @Autowired
    TopicAdapter topicAdapter;
    @Autowired
    TopicThumbService topicThumbService;
    @Autowired
    TopicFavourService topicFavourService;

    /**
     * 发布话题
     *
     * @param request
     * @return
     */
    @PostMapping("/publish")
    @AuthCheck(mustRole = "login")
    public R publishTopic(@RequestBody TopicPublishRequest request) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        Long topicId = topicAppService.addTopic(request, userId);
        return R.ok(topicId);
    }


    /**
     * 发布评论
     *
     * @param request
     * @return
     */
    @PostMapping("/comment")
    @AuthCheck(mustRole = "login")
    public R commentTopic(@RequestBody TopicCommentRequest request) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        Long commentId = topicAppService.commentTopic(request, userId);
        return R.ok(commentId);
    }

    /**
     * 获取评论
     *
     * @param topicId
     * @return
     */
    @GetMapping("/comment/get")
    public R getCommentsByTopicId(@RequestParam("topicId") Long topicId) {
        List<TopicCommentVO> topicCommentVOList = topicAppService.getCommentsByTopicId(topicId);
        return R.ok(topicCommentVOList);
    }

    /**
     * 分页查询 - 帖子
     *
     * @param queryVO
     * @return
     */
    @PostMapping("/list")
    public R list(@RequestBody PageVO queryVO) {
        PageUtils page = topicService.queryPage(queryVO);
        if (!page.getList().isEmpty()) {
            List<TopicVO> topicVOS = topicAdapter.buildTopicVOsByTopics(page.getList());
            page.setList(topicVOS);
        }
        return R.ok().put("data", page);
    }

    /**
     * 游标分页查询
     */
    @PostMapping("/list/cursor")
    @ApiOperation("查询（游标翻页）")
    public R getTopicListByCursor(@RequestBody ForumCursorPageRequest pageRequest) {
        CursorPageBaseVO<TopicVO> cursorPageBaseVO = topicAppService.getTopicPageByCursor(pageRequest);
        return R.ok(cursorPageBaseVO);
    }

    @GetMapping("/detail/get")
    public R getTopicById(@RequestParam("id") Long id) {
        Topic topic = topicService.getById(id);
        TopicVO topicVO = CollectionUtil.getFirst(topicAdapter.buildTopicVOsByTopics(Arrays.asList(topic)));
        List<TopicCommentVO> commentVOS = topicAppService.getCommentsByTopicId(id);
        TopicDetailVO build = TopicDetailVO.builder()
                .topicVO(topicVO)
                .commentVOS(commentVOS)
                .build();
        return R.ok(build);
    }

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @PostMapping("comment/delete/{id}")
    @AuthCheck(mustRole = "login")
    public R deleteComment(@PathVariable("id") Long commentId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        topicAppService.deleteComment(commentId, userId);
        return R.ok();
    }

    /**
     * 帖子点赞与取消
     * @param postThumbAddRequest
     * @return
     */
    @PostMapping("/thumb")
    @AuthCheck(mustRole = "login")
    public R doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest) {
        // 登录才能点赞
        User loginUser = AuthInterceptor.userThreadLocal.get();
        long topicId = postThumbAddRequest.getPostId();
        int result = topicThumbService.doPostThumb(topicId, loginUser);
        return R.ok(result);
    }

    /**
     * 收藏 / 取消收藏
     */
    @PostMapping("/favour")
    @AuthCheck(mustRole = "login")
    public R doFavour(@RequestBody PostFavourAddRequest postFavourAddRequest) {
        // 登录才能操作
        final User loginUser = AuthInterceptor.userThreadLocal.get();
        long topicId = postFavourAddRequest.getPostId();
        int result = topicFavourService.doFavour(topicId, loginUser);
        return R.ok(result);
    }

}
