package com.bitdf.txing.oj.chat.service.business;

import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsOnlineOfflineNotifyVO;
import io.netty.channel.Channel;

/**
 * @author Lizhiwei
 * @date 2024/1/2 19:17:54
 * 注释：
 */
public interface WebSocketService {
    void removed(Channel channel);

    void sendToAllOnline(WsBaseVO<WsOnlineOfflineNotifyVO> buildOffLineNotifyWsVO, Long userId);
}
