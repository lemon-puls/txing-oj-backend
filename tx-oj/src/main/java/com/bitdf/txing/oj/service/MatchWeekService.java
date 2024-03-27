package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.vo.match.WeekMatchVO;

import java.util.List;

/**
 * @author lizhiwei
 * @email
 * @date 2024-03-13 15:00:49
 */
public interface MatchWeekService extends IService<WeekMatch> {
    WeekMatch getLastSessionMatch();

    WeekMatchVO getNextMatch();

    List<WeekMatchVO> getHistoryMatch();

    WeekMatchVO getLastWeekMatch();

//    PageUtils queryPage(Map<String, Object> params);
}

