package com.bitdf.txing.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.entity.match.MatchSubmitRelate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author lizhiwei
 * @email 
 * @date 2024-03-13 15:00:49
 */
@Mapper
public interface MatchSubmitRelateMapper extends BaseMapper<MatchSubmitRelate> {

    List<QuestionSubmit> getSubmitsOfUser(@Param("matchId") Long matchId, @Param("joinRecordId") Long joinRecordId);
}
