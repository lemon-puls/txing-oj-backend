package com.bitdf.txing.oj.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.chat.domain.vo.request.ContactUpdateOrAddRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.RemoveSessionRequest;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
public interface ContactService extends IService<Contact> {
    void updateOrCreateActiveTime(Long roomId, List<Long> targetUserIds, Long msgId, Date createTime);

    Date getUserContactLastMsgTime(Long roomId, Long userId);

    CursorPageBaseVO<Contact> getContactPageByCursor(CursorPageBaseRequest cursorPageBaseRequest, Long userId);

    List<Contact> getByRoomIds(List<Long> roomIds, Long userId);

    Contact getByUserIdAndRoomId(Long userId, Long roomId);

    void removeSession(Long userId, RemoveSessionRequest removeSessionRequest);

    void updateOrCreateContact(Long userId, ContactUpdateOrAddRequest updateOrAddRequest);

//    PageUtils queryPage(Map<String, Object> params);
}

