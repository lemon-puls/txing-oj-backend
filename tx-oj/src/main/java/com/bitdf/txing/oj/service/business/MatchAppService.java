package com.bitdf.txing.oj.service.business;

import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.vo.match.WeekMatchStartVO;

public interface MatchAppService {
    WeekMatchStartVO startMatch();

    Long submitSingle(MatchSubmitSingleRequest matchSubmitSingleRequest);

    Long saveOrUpdateSubmit(QuestionSubmitDoRequest request, Long matchId, Long userId);

    void submitAll(MatchSubmitBatchRequest request);
}
