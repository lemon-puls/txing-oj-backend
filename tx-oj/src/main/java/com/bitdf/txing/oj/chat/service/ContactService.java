package com.bitdf.txing.oj.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
public interface ContactService extends IService<Contact> {
    void updateOrCreateActiveTime(Long id, List<Long> targetUserIds, Long id1, Date createTime);

    Date getUserContactLastMsgTime(Long roomId, Long userId);

    CursorPageBaseVO<Contact> getContactPageByCursor(CursorPageBaseRequest cursorPageBaseRequest, Long userId);

    List<Contact> getByRoomIds(List<Long> roomIds, Long userId);

//    PageUtils queryPage(Map<String, Object> params);
}

