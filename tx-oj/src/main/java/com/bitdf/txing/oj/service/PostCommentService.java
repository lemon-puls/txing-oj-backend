package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.PostComment;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.post.PostCommentVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/3 1:02:51
 * 注释：
 */
public interface PostCommentService extends IService<PostComment> {
    List<PostCommentVO> getPostCommentVOs(List<?> list, User loginUser);

    boolean thumbComment(Long commentId, int opsValue);

    PageUtils queryPage(PageVO queryVO);
}
