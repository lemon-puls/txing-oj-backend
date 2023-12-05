package com.bitdf.txing.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitdf.txing.oj.model.entity.PostComment;
import com.bitdf.txing.oj.model.entity.QuestionComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Lizhiwei
 * @date 2023/12/3 1:01:41
 * 注释：
 */
@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {

}
