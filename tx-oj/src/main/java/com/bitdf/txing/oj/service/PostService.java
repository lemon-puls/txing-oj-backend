package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.post.PostEsDTO;
import com.bitdf.txing.oj.model.dto.post.PostQueryRequest;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.model.vo.post.PostVO;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子服务
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
public interface PostService extends IService<Post> {

    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param postQueryRequest
     * @return
     */
    Page<PostEsDTO> searchFromEs(PostQueryRequest postQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param post
     * @param request
     * @return
     */
    PostVO getPostVO(Post post, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     * @return
     */
    Page<PostEsDTO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);

    List<PostEsDTO> getPostEsDTOByPosts(List<Post> postList);
}
