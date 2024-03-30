package com.bitdf.txing.oj.service;

import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.judge.JudgeInfo;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.dto.question.JudgeConfig;
import com.bitdf.txing.oj.model.dto.submit.QuestionSubmitDoRequest;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;
import com.bitdf.txing.oj.model.vo.match.OnlinePKResultVO;
import com.bitdf.txing.oj.model.vo.match.PkMatchStartVO;
import com.bitdf.txing.oj.utils.page.PageUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatchOnlinepkAppService {
    Long findOpponent();

    Long submit(MatchSubmitSingleRequest request);

    boolean checkAndBuildPkMatchResult(Long matchId);

    Double computeSubmitScore(JudgeInfo judgeInfo, QuestionSubmit questionSubmit,
                              JudgeConfig judgeConfig, Long useSeconds, Long totalSeconds);

    @Transactional(rollbackFor = Exception.class)
    Long saveOrUpdateSubmit(QuestionSubmitDoRequest request, Long matchId, Long userId);

    OnlinePKResultVO getPkResult(Long matchId);

    OnlinePKResultVO buildOnlinePKResultVO(OnlinePkMatch onlinePkMatch);

    List<OnlinePKResultVO> getPkRecords(Long userId);

    PkMatchStartVO startPk(Long matchId);

    boolean cancelMatch(Long userId);

    PageUtils getPkRecordByUser(PageRequest pageRequest, Long userId);
}
