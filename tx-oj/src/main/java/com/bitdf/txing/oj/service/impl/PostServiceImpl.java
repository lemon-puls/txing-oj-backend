package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.constant.CommonConstant;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.PostFavourMapper;
import com.bitdf.txing.oj.mapper.PostMapper;
import com.bitdf.txing.oj.mapper.PostThumbMapper;
import com.bitdf.txing.oj.model.dto.post.PostEsDTO;
import com.bitdf.txing.oj.model.dto.post.PostQueryRequest;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.model.entity.PostFavour;
import com.bitdf.txing.oj.model.entity.PostThumb;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.post.PostVO;
import com.bitdf.txing.oj.model.vo.user.UserVO;
import com.bitdf.txing.oj.service.PostService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.page.SQLFilter;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;

    @Resource
    private PostThumbMapper postThumbMapper;

    @Resource
    private PostFavourMapper postFavourMapper;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        String title = post.getTitle();
        String content = post.getContent();
//        String tags = post.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content), TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 100) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param postQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        Long userId = postQueryRequest.getUserId();
        Long notId = postQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("is_delete", false);
        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortOrder)) {
            queryWrapper.orderBy(SQLFilter.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                    sortField);
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        return queryWrapper;
    }

    @Override
    public Page<PostEsDTO> searchFromEs(PostQueryRequest postQueryRequest) {
        Long id = postQueryRequest.getId();
        Long notId = postQueryRequest.getNotId();
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        List<String> orTagList = postQueryRequest.getOrTags();
        Long userId = postQueryRequest.getUserId();
        String intro = postQueryRequest.getIntro();
        // es 起始页为 0
        long current = postQueryRequest.getCurrent() - 1;
        long pageSize = postQueryRequest.getPageSize();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 必须包含所有标签
//        if (CollectionUtils.isNotEmpty(tagList)) {
//            for (String tag : tagList) {
//                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
//            }
//        }
        // 包含任何一个标签即可
//        if (CollectionUtils.isNotEmpty(orTagList)) {
//            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
//            for (String tag : orTagList) {
//                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
//            }
//            orTagBoolQueryBuilder.minimumShouldMatch(1);
//            boolQueryBuilder.filter(orTagBoolQueryBuilder);
//        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
//            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", searchText));
//            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        } else {
            // 默认排序
            sortBuilder = SortBuilders.fieldSort("createTime");
            sortBuilder.order(SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("intro");
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
//        highlightBuilder.requireFieldMatch(true);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).withHighlightBuilder(highlightBuilder).build();
        SearchHits<PostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostEsDTO.class);
        Page<PostEsDTO> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<PostEsDTO> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<PostEsDTO>> searchHitList = searchHits.getSearchHits();

            List<Long> postIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<Post> postList = baseMapper.selectBatchIds(postIdList);
            if (postList != null) {
                Map<Long, List<Post>> idPostMap = postList.stream().collect(Collectors.groupingBy(Post::getId));
                searchHitList.forEach(searchHit -> {
                    if (idPostMap.containsKey(searchHit.getContent().getId())) {
                        PostEsDTO esDTO = searchHit.getContent();

                        Post post = idPostMap.get(esDTO.getId()).get(0);
                        BeanUtils.copyProperties(post, esDTO);

                        Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
                        for (Map.Entry<String, List<String>> entry : highlightFields.entrySet()) {
                            List<String> stringList = highlightFields.get(entry.getKey());
                            for (String s : stringList) {
                                String tempStr = "";
                                tempStr = s.replaceAll("<font color='red'>", "");
                                tempStr = tempStr.replaceAll("</font>", "");
                                if (entry.getKey().equals("title")) {
                                    esDTO.setTitle(esDTO.getTitle().replace(tempStr, s));
                                } else if (entry.getKey().equals("intro")) {
                                    esDTO.setIntro(esDTO.getIntro().replace(tempStr, s));
                                } else if (entry.getKey().equals("content")) {
                                    esDTO.setContent(esDTO.getContent().replace(tempStr, s));
                                }
                            }
                        }
                        resourceList.add(esDTO);
                    }
                    else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(searchHit.getContent().getId()), PostEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
//                postIdList.forEach(postId -> {
//                    if (idPostMap.containsKey(postId)) {
//                        resourceList.add(idPostMap.get(postId).get(0));
//                    } else {
//                        // 从 es 清空 db 已物理删除的数据
//                        String delete = elasticsearchRestTemplate.delete(String.valueOf(postId), PostEsDTO.class);
//                        log.info("delete post {}", delete);
//                    }
//                });
            }
        }
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public PostVO getPostVO(Post post, HttpServletRequest request) {
        PostVO postVO = PostVO.objToVo(post);
        long postId = post.getId();
        // 1. 关联查询用户信息
        Long userId = post.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull();
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
            postThumbQueryWrapper.in("post_id", postId);
            postThumbQueryWrapper.eq("user_id", loginUser.getId());
            PostThumb postThumb = postThumbMapper.selectOne(postThumbQueryWrapper);
            postVO.setHasThumb(postThumb != null);
            // 获取收藏
            QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>();
            postFavourQueryWrapper.in("post_id", postId);
            postFavourQueryWrapper.eq("user_id", loginUser.getId());
            PostFavour postFavour = postFavourMapper.selectOne(postFavourQueryWrapper);
            postVO.setHasFavour(postFavour != null);
        }
        return postVO;
    }

    /**
     * 封装分页结果
     *
     * @param postPage
     * @param request
     * @return
     */
    @Override
    public Page<PostEsDTO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        List<Post> postList = postPage.getRecords();
        Page<PostEsDTO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollectionUtils.isEmpty(postList)) {
            return postVOPage;
        }
        List<PostEsDTO> postEsDTOByPosts = getPostEsDTOByPosts(postList);
        postVOPage.setRecords(postEsDTOByPosts);
        return postVOPage;
    }

//    @Override
//    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
//        List<Post> postList = postPage.getRecords();
//        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
//        if (CollectionUtils.isEmpty(postList)) {
//            return postVOPage;
//        }
//        // 1. 关联查询用户信息
//        Set<Long> userIdSet = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
//        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
//                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> postIdSet = postList.stream().map(Post::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
//            postThumbQueryWrapper.in("postId", postIdSet);
//            postThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<PostThumb> postPostThumbList = postThumbMapper.selectList(postThumbQueryWrapper);
//            postPostThumbList.forEach(postPostThumb -> postIdHasThumbMap.put(postPostThumb.getPostId(), true));
//            // 获取收藏
//            QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>();
//            postFavourQueryWrapper.in("postId", postIdSet);
//            postFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<PostFavour> postFavourList = postFavourMapper.selectList(postFavourQueryWrapper);
//            postFavourList.forEach(postFavour -> postIdHasFavourMap.put(postFavour.getPostId(), true));
//        }
//        // 填充信息
//        List<PostVO> postVOList = postList.stream().map(post -> {
//            PostVO postVO = PostVO.objToVo(post);
//            Long userId = post.getUserId();
//            User user = null;
//            if (userIdUserListMap.containsKey(userId)) {
//                user = userIdUserListMap.get(userId).get(0);
//            }
//            postVO.setUser(userService.getUserVO(user));
//            postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
//            postVO.setHasFavour(postIdHasFavourMap.getOrDefault(post.getId(), false));
//            return postVO;
//        }).collect(Collectors.toList());
//        postVOPage.setRecords(postVOList);
//        return postVOPage;
//    }

    /**
     * List<Post> ==>  List<PostEsDTO>
     *
     * @param postList
     * @return
     */
    @Override
    public List<PostEsDTO> getPostEsDTOByPosts(List<Post> postList) {
        List<PostEsDTO> collect = postList.stream().map((item) -> {
            PostEsDTO postEsDTO = new PostEsDTO();
            BeanUtils.copyProperties(item, postEsDTO);
            // 查询用户
            User user = userService.getById(postEsDTO.getUserId());
            postEsDTO.setUserName(user.getUserName());
            postEsDTO.setUserAvatar(user.getUserAvatar());
            return postEsDTO;
        }).collect(Collectors.toList());
        return collect;
    }

}




