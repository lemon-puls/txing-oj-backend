package com.bitdf.txing.oj.model.vo.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekMatchUserRecordVO {
    private Long matchId;
    private Long joinId;
    /**
     * 比赛名称
     */
    private String name;
    /**
     * 比赛开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 比赛用时
     */
    private Long useSeconds;
    /**
     * ac题目数
     */
    private Integer acCount;
    /**
     * 乘积排行
     */
    private Integer gradeRank;
    /**
     * 参赛总人数
     */
    private Integer joinCount;
    /**
     * 本场比赛赢得积分
     */
    private Integer score;
}
