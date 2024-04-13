package com.bitdf.txing.oj.model.vo.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author lizhiwei
 * @date 2024/4/12 21:46
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekSimulateRecordVO {

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
}
