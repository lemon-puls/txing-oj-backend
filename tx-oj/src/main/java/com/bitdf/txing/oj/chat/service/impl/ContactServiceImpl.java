package com.bitdf.txing.oj.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.domain.vo.request.ContactUpdateOrAddRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.RemoveSessionRequest;
import com.bitdf.txing.oj.chat.enume.RoomStatusEnum;
import com.bitdf.txing.oj.chat.mapper.ContactMapper;
import com.bitdf.txing.oj.chat.service.ContactService;
import com.bitdf.txing.oj.chat.service.cache.RoomCache;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.utils.CursorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service("contactService")
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {

    @Autowired
    RoomCache roomCache;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<ContactEntity> page = this.page(
//                new Query<ContactEntity>().getPage(params),
//                new QueryWrapper<ContactEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    @Override
    public void updateOrCreateActiveTime(Long roomId, List<Long> targetUserIds, Long msgId, Date createTime) {
        baseMapper.updateOrCreateActiveTime(roomId, targetUserIds, msgId, createTime);
    }

    /**
     * 获取用户会话的最后一条消息的时间
     *
     * @param roomId
     * @param userId
     * @return
     */
    @Override
    public Date getUserContactLastMsgTime(Long roomId, Long userId) {
        Room room = roomCache.get(roomId);
        ThrowUtils.throwIf(room == null, "房间号错误");
        if (room.getHotFlag()) {
            return null;
        }
        Contact contact = lambdaQuery().eq(Contact::getUserId, userId)
                .eq(Contact::getRoomId, roomId)
                .one();
        return contact == null ? null : contact.getActiveTime();
    }

    @Override
    public CursorPageBaseVO<Contact> getContactPageByCursor(CursorPageBaseRequest cursorPageBaseRequest, Long userId) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseRequest, wrapper -> {
            wrapper.eq(Contact::getUserId, userId)
                    .eq(Contact::getStatus, RoomStatusEnum.ACTIVE.getCode());
        }, Contact::getActiveTime);
    }

    @Override
    public List<Contact> getByRoomIds(List<Long> roomIds, Long userId) {
        return lambdaQuery().in(Contact::getRoomId, roomIds)
                .eq(Contact::getUserId, userId)
                .list();
    }

    /**
     * @param userId
     * @param roomId
     * @return
     */
    @Override
    public Contact getByUserIdAndRoomId(Long userId, Long roomId) {
        return lambdaQuery().eq(Contact::getUserId, userId)
                .eq(Contact::getRoomId, roomId)
                .one();
    }

    /**
     * 移除会话
     *
     * @param userId
     * @param removeSessionRequest
     */
    @Override
    public void removeSession(Long userId, RemoveSessionRequest removeSessionRequest) {
        lambdaUpdate()
                .eq(Contact::getRoomId, removeSessionRequest.getRoomId())
                .eq(Contact::getUserId, userId)
                .set(Contact::getStatus, RoomStatusEnum.FORBIDDEN.getCode())
                .update();
    }

    @Override
    public void updateOrCreateContact(Long userId, ContactUpdateOrAddRequest updateOrAddRequest) {
//        // 判断会话是否创建过
//        Contact contact = getByUserIdAndRoomId(userId, updateOrAddRequest.getRoomId());
//        if ()
        updateOrCreateActiveTime(updateOrAddRequest.getRoomId(), Arrays.asList(userId), null, new Date());
    }
}
