package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.match.MatchUserRelate;

/**
 * @author lizhiwei
 * @email
 * @date 2024-03-13 15:00:49
 */
public interface MatchUserRelateService extends IService<MatchUserRelate> {
    MatchUserRelate getByMatchIdAndUserId(Long matchId, Long userId);

    MatchUserRelate getLastJoinRecord(Long matchId, Long userId);

    Long saveEndTime(Long matchId, Long userId);

    MatchUserRelate getSimulateMatchRunning(Long userId);

    void updateUserStatus(Integer code, Long userId, Long matchId);

//    PageUtils queryPage(Map<String, Object> params);
}

