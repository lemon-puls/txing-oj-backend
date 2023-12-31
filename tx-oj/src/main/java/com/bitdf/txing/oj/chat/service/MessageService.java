package com.bitdf.txing.oj.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.chat.domain.vo.request.MessagePageRequest;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;

import java.util.Date;
import java.util.Map;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
public interface MessageService extends IService<Message> {
    CursorPageBaseVO<Message> getPageByCursor(Long roomId, CursorPageBaseRequest pageRequest, Date lastMsgTime);

    Integer getUnReadCount(Long roomId, Date readTime);

//    PageUtils queryPage(Map<String, Object> params);
}

