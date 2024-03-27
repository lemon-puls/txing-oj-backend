package com.bitdf.txing.oj.controller.forum;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicCommentRequest;
import com.bitdf.txing.oj.model.dto.forum.TopicPublishRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.forum.TopicCommentVO;
import com.bitdf.txing.oj.model.vo.forum.TopicDetailVO;
import com.bitdf.txing.oj.model.vo.forum.TopicVO;
import com.bitdf.txing.oj.service.TopicAppService;
import com.bitdf.txing.oj.service.TopicService;
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
    public R getTopicListByCursor(@RequestBody CursorPageBaseRequest pageRequest) {
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


}
