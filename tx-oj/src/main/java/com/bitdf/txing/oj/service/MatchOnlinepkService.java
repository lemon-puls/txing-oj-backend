package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;

import java.util.List;

/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2024-03-13 15:00:49
 */
public interface MatchOnlinepkService extends IService<OnlinePkMatch> {
    OnlinePkMatch getRunningByUserId(Long userId);

    void finishMatch(Long matchId, Long userId);

    OnlinePkMatch isFinished(Long matchId);

    void updateMatchStatus(Long matchId, Integer code);

    List<OnlinePkMatch> getMatchsByUserId(Long userId);

//    PageUtils queryPage(Map<String, Object> params);
}

