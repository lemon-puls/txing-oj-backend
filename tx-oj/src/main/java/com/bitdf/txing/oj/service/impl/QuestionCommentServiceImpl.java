package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.QuestionCommentMapper;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.entity.QuestionComment;
import com.bitdf.txing.oj.model.entity.User;
import com.bitdf.txing.oj.model.vo.question.QuestionCommentVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.QuestionCommentService;

import java.util.List;
import java.util.stream.Collectors;


@Service("questionCommentService")
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment> implements QuestionCommentService {

    @Autowired
    UserService userService;

    @Override
    public PageUtils queryPage(PageVO queryVO) {

        QueryWrapper<QuestionComment> wrapper = new QueryWrapper<>();

        IPage<QuestionComment> iPage = new Query<QuestionComment>().buildWrapperAndPage(wrapper, queryVO, null);

        IPage<QuestionComment> page = this.page(iPage, wrapper);
        return new PageUtils(page);
    }

    /**
     * 把QuestionComment集合转换为QuestionCommentVO集合
     *
     * @param list
     * @return
     */
    @Override
    public List<QuestionCommentVO> getQuestionCommentVOs(List<?> list) {
        List<QuestionCommentVO> collect = list.stream().map((item) -> {
            QuestionComment questionComment = (QuestionComment) item;
            QuestionCommentVO questionCommentVO = new QuestionCommentVO();
            BeanUtils.copyProperties(questionComment, questionCommentVO);
            // 获取对应的用户
            User user = userService.getById(questionCommentVO.getUserId());
            ThrowUtils.throwIf(user == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
            // 设置用户名
            questionCommentVO.setUserName(user.getUserName());
            // 设置头像
            questionCommentVO.setUserAvatar(user.getUserAvatar());
            // TODO 判断是否点赞
            questionCommentVO.setIsFavour(true);
            return questionCommentVO;
        }).collect(Collectors.toList());
        return collect;
    }

}