package com.bitdf.txing.oj.chat.service.adapter;

import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.enume.WsRespTypeEnum;

/**
 * @author Lizhiwei
 * @date 2023/12/30 21:15:51
 * 注释：
 */
public class WsAdapter {

    public static WsBaseVO<ChatMessageVO> buildMsgSend(ChatMessageVO chatMessageVO) {
        WsBaseVO<ChatMessageVO> wsBaseVO = new WsBaseVO<>();
        wsBaseVO.setType(WsRespTypeEnum.MESSAGE.getType());
        wsBaseVO.setData(chatMessageVO);
        return wsBaseVO;
    }
}
