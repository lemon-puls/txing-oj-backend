package com.bitdf.txing.oj.service.adapter;

import com.bitdf.txing.oj.model.entity.match.MatchUserRelate;
import com.bitdf.txing.oj.model.entity.match.WeekMatch;
import com.bitdf.txing.oj.model.vo.match.WeekMatchUserRecordVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchVO;
import com.bitdf.txing.oj.service.MatchWeekService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchWeekAdapter {

    @Autowired
    MatchWeekService matchWeekService;

    public static List<WeekMatchVO> buildWeekMatchVOs(List<WeekMatch> weekMatches) {
        return weekMatches.stream().map(item -> {
            WeekMatchVO weekMatchVO = new WeekMatchVO();
            BeanUtils.copyProperties(item, weekMatchVO);
            return weekMatchVO;
        }).collect(Collectors.toList());
    }

    public List<WeekMatchUserRecordVO> buildMatchUserRecordVOByRelates(List<MatchUserRelate> matchUserRelates) {
        List<WeekMatchUserRecordVO> collect = matchUserRelates.stream().map(matchUserRelate -> {
            WeekMatch weekMatch = matchWeekService.getById(matchUserRelate.getMatchId());
            // 计算该用户比赛用时 以秒为单位
            Long useSeconds = ((matchUserRelate.getEndTime().getTime() - matchUserRelate.getStartTime().getTime()) / 1000);
            WeekMatchUserRecordVO userRecordVO = WeekMatchUserRecordVO.builder()
                    .matchId(weekMatch.getId())
                    .name(weekMatch.getName())
                    .joinId(matchUserRelate.getId())
                    .startTime(weekMatch.getStartTime())
                    .acCount(matchUserRelate.getAcCount())
                    .gradeRank(matchUserRelate.getGradeRank())
                    .joinCount(weekMatch.getJoinCount())
                    .score(matchUserRelate.getScore())
                    .useSeconds(useSeconds)
                    .build();
            return userRecordVO;
        }).collect(Collectors.toList());
        return collect;
    }

}
