package com.bitdf.txing.oj.model.vo.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekMatchRankVO {

    /**
     *  上一场周赛id
     */
    private Long matchId;
    /**
     * 上一场周赛名称
     */
    private String name;
    /**
     * 成绩排名
     */
    private Integer gradeRank;
    /**
     * 获得分数
     */
    private Integer score;
    /**
     * Ac题目数
     */
    private Integer acCount;

    List<WeekMatchRankItemVO> rankItems;
}
