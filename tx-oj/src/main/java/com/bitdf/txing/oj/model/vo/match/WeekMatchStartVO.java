package com.bitdf.txing.oj.model.vo.match;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitdf.txing.oj.model.vo.question.QuestionVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekMatchStartVO {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 状态(0:未开始 1：进行中 2：已结束)
     */
    private Integer status;
    /**
     * 参赛人数
     */
    private Integer joinCount;
    /**
     * 题目集合
     */
    private List<QuestionVO> questions;

    /**
     * 当前用户开始作答时间（模拟赛时需要）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date simulateStartTime;
}
