package com.bitdf.txing.oj.model.dto.match;

import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchSubmitBatchRequest {
    /**
     * 竞赛id
     */
    private Long matchId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 作答集合
     */
    List<QuestionSubmitDoRequest> submits;
}
