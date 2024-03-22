package com.bitdf.txing.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;
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
public interface MatchOnlinepkMapper extends BaseMapper<OnlinePkMatch> {

    void finishMatch(@Param("matchId") Long matchId, @Param("userId") Long userId);

    List<OnlinePkMatch> getMatchsByUserId(@Param("userId") Long userId);
}
