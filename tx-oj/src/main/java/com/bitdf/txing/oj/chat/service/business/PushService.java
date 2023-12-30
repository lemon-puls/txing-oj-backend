package com.bitdf.txing.oj.chat.service.business;

import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:14:56
 * 注释：
 */
public interface PushService {
    void sendPushMsg(WsBaseVO<?> wsBaseVO, Long id);
}
