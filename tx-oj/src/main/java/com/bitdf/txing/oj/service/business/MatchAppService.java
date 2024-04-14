package com.bitdf.txing.oj.service.business;

import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.Question;
import com.bitdf.txing.oj.model.vo.match.MatchResultVO;
import com.bitdf.txing.oj.model.vo.match.WeekMatchStartVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatchAppService {
    WeekMatchStartVO startMatch(Long matchId);

    Long submitSingle(MatchSubmitSingleRequest matchSubmitSingleRequest);

    Long saveOrUpdateSubmit(QuestionSubmitDoRequest request, Long matchId, Long userId, Long joinRecordId);

    void submitAll(MatchSubmitBatchRequest request);

    @Transactional(rollbackFor = Exception.class)
    boolean buildMatchResult(Long matchId);

    void computeMatchRank(Long matchId);

    PageUtils getWeekMatchRecordByUserId(PageRequest pageRequest, Long userId);

    PageUtils getWeekSimulateRecordByUserId(PageRequest pageRequest, Long userId);

    Boolean isRepeatJoin(Long matchId, Long userId);

    MatchResultVO getSimulateResult(Long joinId, Long userId);

    List<Question> getQuestionsByMatchId(Long matchId);
}
