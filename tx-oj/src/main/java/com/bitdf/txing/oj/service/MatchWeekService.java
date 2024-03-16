package com.bitdf.txing.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.vo.match.WeekMatchVO;

/**
 * @author lizhiwei
 * @email
 * @date 2024-03-13 15:00:49
 */
public interface MatchWeekService extends IService<WeekMatch> {
    WeekMatch getLastSessionMatch();

    WeekMatchVO getNextMatch();

//    PageUtils queryPage(Map<String, Object> params);
}

