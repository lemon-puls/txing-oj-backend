package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.QuestionComment;
import com.bitdf.txing.oj.model.entity.User;
import com.bitdf.txing.oj.model.vo.question.QuestionCommentVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;

import java.util.List;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2023-11-13 21:54:02
 */
public interface QuestionCommentService extends IService<QuestionComment> {

    PageUtils queryPage(PageVO queryVO);

    List<QuestionCommentVO> getQuestionCommentVOs(List<?> list, User loginUser);

    boolean thumbComment(Long commentId, int opsValue);
}

