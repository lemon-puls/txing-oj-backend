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
public class WeekMatchVO {
    /**
     * id
     */
    private Long id;
    /**
     * 竞赛名称
     */
    private String name;
    /**
     * 周赛场次（第几场周赛）
     */
    private Integer sessionNo;
    /**
     * 竞赛开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    /**
     * 状态(0:未开始 1：进行中 2：已结束 3: 已完成判题)
     */
    private Integer status;
    /**
     * 参赛人数
     */
    private Integer joinCount;
}
