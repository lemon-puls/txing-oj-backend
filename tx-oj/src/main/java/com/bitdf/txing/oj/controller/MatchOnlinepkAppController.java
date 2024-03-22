package com.bitdf.txing.oj.controller;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.chat.service.adapter.WsAdapter;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitSingleRequest;
import com.bitdf.txing.oj.model.entity.match.OnlinePkMatch;
import com.bitdf.txing.oj.model.vo.match.OnlinePKResultVO;
import com.bitdf.txing.oj.model.vo.match.PkMatchStartVO;
import com.bitdf.txing.oj.model.vo.match.WsOnlinePkTeamUpVO;
import com.bitdf.txing.oj.service.MatchOnlinepkAppService;
import com.bitdf.txing.oj.service.MatchOnlinepkService;
import com.bitdf.txing.oj.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author lizhiwei
 * @email
 * @date 2024-03-13 15:00:49
 */
@RestController
@RequestMapping("/match/online/pk")
public class MatchOnlinepkAppController {
    @Autowired
    private MatchOnlinepkService matchOnlinepkService;
    @Autowired
    private MatchOnlinepkAppService matchOnlinepkAppService;

    /**
     * 匹配对手
     */
    @GetMapping("/find")
    @AuthCheck(mustRole = "login")
    public R findOpponent() {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        OnlinePkMatch oldPkMatch = matchOnlinepkService.getRunningByUserId(userId);
        if (oldPkMatch != null) {
            return R.ok(10001, "当前有未完成的比赛， 请先完成！").put("data", oldPkMatch.getId());
        }
        Long matchId = matchOnlinepkAppService.findOpponent();
        return R.ok(matchId);
    }

    /**
     * 提交作答(或比赛结束)
     */
    @PostMapping("/submit")
    @AuthCheck(mustRole = "login")
    public R pkSubmit(@RequestBody MatchSubmitSingleRequest request) {
        Long submitId = matchOnlinepkAppService.submit(request);
        return R.ok(submitId);
    }

    /**
     * 查询PK结果
     */
    @GetMapping("/result/get")
    @AuthCheck(mustRole = "login")
    public R getPkResult(@RequestParam("matchId") Long matchId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        OnlinePKResultVO resultVO = matchOnlinepkAppService.getPkResult(matchId);
        return R.ok(resultVO);
    }

    /**
     * 获取当前的PK记录
     */
    @GetMapping("/records/get")
    @AuthCheck(mustRole = "login")
    public R getPkRecords() {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        List<OnlinePKResultVO> resultVOs = matchOnlinepkAppService.getPkRecords(userId);
        return R.ok(resultVOs);
    }

    /**
     * 开始pK
     */
    @GetMapping("/start")
    @AuthCheck(mustRole = "login")
    public R startPk(@RequestParam("matchId") Long matchId) {
        PkMatchStartVO startVO = matchOnlinepkAppService.startPk(matchId);
        return R.ok(startVO);
    }

    /**
     * 取消匹配
     */
    @GetMapping("/cancel")
    @AuthCheck(mustRole = "login")
    public R cancelMatch() {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        boolean b = matchOnlinepkAppService.cancelMatch(userId);
        return R.ok(b);
    }

    @Autowired
    PushService pushService;

    @GetMapping("/ceshi")
    public R ceshi() {
        pushService.sendPushMsg(
                WsAdapter.buildPKTeamUpNotifyVO(new WsOnlinePkTeamUpVO(1L)),
                Arrays.asList(1L), new Date().getTime());
        return R.ok();
    }

}
