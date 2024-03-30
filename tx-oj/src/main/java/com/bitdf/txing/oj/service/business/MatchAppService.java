package com.bitdf.txing.oj.service.business;

import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.vo.match.WeekMatchStartVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import org.springframework.transaction.annotation.Transactional;

public interface MatchAppService {
    WeekMatchStartVO startMatch(Long matchId);

    Long submitSingle(MatchSubmitSingleRequest matchSubmitSingleRequest);

    Long saveOrUpdateSubmit(QuestionSubmitDoRequest request, Long matchId, Long userId, Long joinRecordId);

    void submitAll(MatchSubmitBatchRequest request);

    @Transactional(rollbackFor = Exception.class)
    boolean buildMatchResult(Long matchId);

    void computeMatchRank(Long matchId);

    PageUtils getWeekMatchRecordByUserId(PageRequest pageRequest, Long userId);
}
