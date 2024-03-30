package com.bitdf.txing.oj.model.vo.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekMatchRankItemVO implements Serializable {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户排名
     */
    private Integer rank;
    /**
     * 本场获得积分
     */
    private Integer score;
}
