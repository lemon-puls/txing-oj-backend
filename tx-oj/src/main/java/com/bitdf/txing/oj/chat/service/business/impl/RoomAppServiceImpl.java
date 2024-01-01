package com.bitdf.txing.oj.chat.service.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.bitdf.txing.oj.chat.domain.enume.RoomTypeEnum;
import com.bitdf.txing.oj.chat.domain.vo.RoomBaseInfo;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatRoomVO;
import com.bitdf.txing.oj.chat.service.ContactService;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.chat.service.RoomFriendService;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.adapter.ChatAdapter;
import com.bitdf.txing.oj.chat.service.business.RoomAppService;
import com.bitdf.txing.oj.chat.service.cache.HotRoomCache;
import com.bitdf.txing.oj.chat.service.cache.RoomCache;
import com.bitdf.txing.oj.chat.service.cache.RoomFriendCache;
import com.bitdf.txing.oj.chat.service.cache.RoomGroupCache;
import com.bitdf.txing.oj.chat.service.strategy.AbstractMsghandler;
import com.bitdf.txing.oj.chat.service.strategy.MsgHandlerFactory;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.*;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/31 14:50:09
 * 注释：
 */
@Service
public class RoomAppServiceImpl implements RoomAppService {

    @Autowired
    ContactService contactService;
    @Autowired
    HotRoomCache hotRoomCache;
    @Autowired
    RoomCache roomCache;
    @Autowired
    RoomFriendCache roomFriendCache;
    @Autowired
    RoomGroupCache roomGroupCache;
    @Autowired
    UserCache userCache;
    @Autowired
    MessageService messageService;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomFriendService roomFriendService;

    @Override
    public CursorPageBaseVO<ChatRoomVO> getContactPageByCursor(CursorPageBaseRequest cursorPageBaseRequest, Long userId) {
        Double hotEnd = getCursorOrNull(cursorPageBaseRequest.getCursor());
        Double hotStart = null;
        // 获取普通会话
        CursorPageBaseVO<Contact> contactCursorPageBaseVO = contactService.getContactPageByCursor(cursorPageBaseRequest, userId);
        List<Long> roomIds = contactCursorPageBaseVO.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
        if (!contactCursorPageBaseVO.getIsLast()) {
            hotStart = getCursorOrNull(contactCursorPageBaseVO.getCursor());
        }
        // 获取热门会话
        Set<ZSetOperations.TypedTuple<String>> typedTuples = hotRoomCache.getRoomRange(hotStart, hotEnd);
        List<Long> hotRoomIds = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull)
                .map(Long::parseLong).collect(Collectors.toList());
        roomIds.addAll(hotRoomIds);
        // 组装会话信息（名称 头像 未读数等等）
        List<ChatRoomVO> result = buildContactResp(userId, roomIds);
        return CursorPageBaseVO.init(contactCursorPageBaseVO, result);
    }

    /**
     * @param userId
     * @param roomIds
     * @return
     */
    @Override
    public List<ChatRoomVO> buildContactResp(Long userId, List<Long> roomIds) {
        // 获取room基本信息
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIds, userId);
        // 获取最后一条消息
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(roomBaseInfo -> {
            return roomBaseInfo.getLastMsgId();
        }).collect(Collectors.toList());
        List<Message> messages = msgIds.isEmpty() ? new ArrayList<>() : messageService.listByIds(msgIds);
        Map<Long, Message> messageMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        // 获取消息发送者信息
        List<Long> fromUserIds = messages.stream().map(Message::getFromUserId).collect(Collectors.toList());
        Map<Long, User> userMap = userCache.getBatch(fromUserIds);
        // 获取room未读消息数
        Map<Long, Integer> unReadCountMap = getUnReadCountMap(userId, roomIds);
        return roomBaseInfoMap.values().stream().map(roomBaseInfo -> {
                    ChatRoomVO chatRoomVO = new ChatRoomVO();
                    chatRoomVO.setAvatar(roomBaseInfo.getAvatar());
                    chatRoomVO.setRoomId(roomBaseInfo.getRoomId());
                    chatRoomVO.setActiveTime(roomBaseInfo.getActiveTime());
                    chatRoomVO.setHotFlag(roomBaseInfo.getHotFlag());
                    chatRoomVO.setType(roomBaseInfo.getType());
                    chatRoomVO.setName(roomBaseInfo.getName());
                    Message message = messageMap.get(roomBaseInfo.getLastMsgId());
                    if (Objects.nonNull(message)) {
                        AbstractMsghandler msghandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
                        String text = msghandler.showContactMsg(message);
                        chatRoomVO.setText(userMap.get(message.getFromUserId()).getUserName() + ":" + text);
                    }
                    chatRoomVO.setUnreadCount(unReadCountMap.getOrDefault(roomBaseInfo.getRoomId(), 0));
                    return chatRoomVO;
                }).sorted(Comparator.comparing(ChatRoomVO::getActiveTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取room未读数
     *
     * @param userId
     * @param roomIds
     * @return
     */
    private Map<Long, Integer> getUnReadCountMap(Long userId, List<Long> roomIds) {
        List<Contact> contacts = contactService.getByRoomIds(roomIds, userId);
        return contacts.parallelStream().map(contact -> {
            Integer unReadCount = messageService.getUnReadCount(contact.getRoomId(), contact.getReadTime());
            return Pair.of(contact.getRoomId(), unReadCount);
        }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long userId) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        Map<Integer, List<Long>> groupedRoomIdMap = roomMap.values().stream().collect(
                Collectors.groupingBy(Room::getType, Collectors.mapping(Room::getId, Collectors.toList())));
        // 获取群组信息
        List<Long> groupRoomIds = groupedRoomIdMap.get(RoomTypeEnum.GROUP.getCode());
        Map<Long, RoomGroup> roomGroupMap = roomGroupCache.getBatch(groupRoomIds);
        // 获取用户
        List<Long> friendRoomIds = groupedRoomIdMap.get(RoomTypeEnum.FRIEND.getCode());
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRoomIds, userId);

        return roomMap.values().stream().map(room -> {
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            roomBaseInfo.setRoomId(room.getId());
            roomBaseInfo.setType(room.getType());
            roomBaseInfo.setHotFlag(room.getHotFlag());
            roomBaseInfo.setActiveTime(room.getActiveTime());
            roomBaseInfo.setLastMsgId(room.getMsgId());
            if (RoomTypeEnum.FRIEND.getCode().equals(room.getType())) {
                User user = friendRoomMap.get(room.getId());
                roomBaseInfo.setAvatar(user.getUserAvatar());
                roomBaseInfo.setName(user.getUserName());
            } else if (RoomTypeEnum.GROUP.getCode().equals(room.getType())) {
                RoomGroup roomGroup = roomGroupMap.get(room.getId());
                roomBaseInfo.setName(roomGroup.getName());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
            }
            return roomBaseInfo;
        }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    /**
     * key: roomId value: User
     *
     * @param friendRoomIds
     * @param userId
     * @return
     */
    private Map<Long, User> getFriendRoomMap(List<Long> friendRoomIds, Long userId) {
        if (CollectionUtil.isEmpty(friendRoomIds)) {
            return new HashMap<>();
        }
        Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(friendRoomIds);
        Set<Long> friendIdSet = ChatAdapter.getFriendIdSet(roomFriendMap.values(), userId);
        Map<Long, User> userMap = userCache.getBatch(new ArrayList<>(friendIdSet));
        Map<Long, User> collect = roomFriendMap.values().stream().collect(Collectors.toMap(RoomFriend::getRoomId, (roomFriend -> {
            Long friendId = ChatAdapter.getFriendId(roomFriend, userId);
            return userMap.get(friendId);
        })));
        return collect;
    }

    private Double getCursorOrNull(String cursor) {
        Double aDouble = Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
        return aDouble;
    }

    /**
     * 获取会话详情（By roomId）
     *
     * @param roomId
     * @param userId
     * @return
     */
    @Override
    public ChatRoomVO getContactDetailByRoomId(Long roomId, Long userId) {
        List<ChatRoomVO> chatRoomVOS = buildContactResp(userId, Collections.singletonList(roomId));
        return chatRoomVOS.get(0);
    }

    /**
     * 获取会话详情（By FriendId）
     *
     * @param userId
     * @param friendId
     * @return
     */
    @Override
    public ChatRoomVO getContactDetailByFriendId(Long userId, Long friendId) {
        List<Long> lists = ChatAdapter.sortUserIdList(Arrays.asList(userId, friendId));
        RoomFriend roomFriend = roomFriendService.getByUserIds(lists.get(0), lists.get(1));
        List<ChatRoomVO> chatRoomVOS = buildContactResp(userId, Collections.singletonList(roomFriend.getRoomId()));
        return chatRoomVOS.get(0);
    }
}
