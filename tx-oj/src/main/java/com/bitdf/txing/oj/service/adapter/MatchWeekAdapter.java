package com.bitdf.txing.oj.service.adapter;

import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.vo.match.WeekMatchVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MatchWeekAdapter {

    public static List<WeekMatchVO> buildWeekMatchVOs(List<WeekMatch> weekMatches) {
        return weekMatches.stream().map(item -> {
            WeekMatchVO weekMatchVO = new WeekMatchVO();
            BeanUtils.copyProperties(item, weekMatchVO);
            return weekMatchVO;
        }).collect(Collectors.toList());
    }
}
