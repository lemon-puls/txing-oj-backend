package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.QuestionCommentMapper;
import com.bitdf.txing.oj.model.entity.QuestionComment;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.question.QuestionCommentVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.RedisUtils;
import com.bitdf.txing.oj.utils.page.PageUtils;
import com.bitdf.txing.oj.utils.page.PageVO;
import com.bitdf.txing.oj.utils.page.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.QuestionCommentService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service("questionCommentService")
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment> implements QuestionCommentService {

    @Autowired
    UserService userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

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
     * @param loginUser
     * @return
     */
    @Override
    public List<QuestionCommentVO> getQuestionCommentVOs(List<?> list, User loginUser) {
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
            // 判断是否点赞
            if (loginUser != null) {
                BoundSetOperations<String, String> boundSetOps = stringRedisTemplate
                        .boundSetOps(RedisUtils.getQuestionCommentThumbKey(questionComment.getQuestionId(), questionComment.getId()));
                Boolean isMember = boundSetOps.isMember(loginUser.getId().toString());
                questionCommentVO.setIsFavour(isMember);
            } else {
                questionCommentVO.setIsFavour(false);
            }
//            // TODO 转换时间 直接返回会丢失秒数 暂时未找到解决方案 有时间了需解决一下
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String format = simpleDateFormat.format(questionComment.getCreateTime());
//            questionCommentVO.setCreateTime(format);
            return questionCommentVO;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 更新问题评论点赞数
     *
     * @param commentId
     * @param opsValue
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean thumbComment(Long commentId, int opsValue) {
        UpdateWrapper<QuestionComment> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(QuestionComment::getId, commentId)
                .gt(opsValue == -1, QuestionComment::getFavourNum, 0)
                .setSql(opsValue == -1, "favour_num = favour_num - 1")
                .setSql(opsValue == 1, "favour_num = favour_num + 1");
        boolean update = this.update(wrapper);
        return update;
    }

}
