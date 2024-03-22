package com.bitdf.txing.oj.service;

import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.vo.match.OnlinePKResultVO;
import com.bitdf.txing.oj.model.vo.match.PkMatchStartVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatchOnlinepkAppService {
    Long findOpponent();

    Long submit(MatchSubmitSingleRequest request);

    @Transactional(rollbackFor = Exception.class)
    Long saveOrUpdateSubmit(QuestionSubmitDoRequest request, Long matchId, Long userId);

    OnlinePKResultVO getPkResult(Long matchId);

    List<OnlinePKResultVO> getPkRecords(Long userId);

    PkMatchStartVO startPk(Long matchId);

    boolean cancelMatch(Long userId);
}
