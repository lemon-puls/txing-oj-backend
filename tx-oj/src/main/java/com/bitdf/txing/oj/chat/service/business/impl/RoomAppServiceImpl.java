package com.bitdf.txing.oj.chat.service.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.bitdf.txing.oj.chat.domain.vo.RoomBaseInfo;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupAddRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupMemberRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMemberVO;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatRoomVO;
import com.bitdf.txing.oj.chat.domain.vo.response.GroupDetailVO;
import com.bitdf.txing.oj.chat.enume.GroupRoleEnum;
import com.bitdf.txing.oj.chat.enume.RoomTypeEnum;
import com.bitdf.txing.oj.chat.event.GroupMemberAddEvent;
import com.bitdf.txing.oj.chat.service.*;
import com.bitdf.txing.oj.chat.service.adapter.ChatAdapter;
import com.bitdf.txing.oj.chat.service.adapter.MemberAdapter;
import com.bitdf.txing.oj.chat.service.business.RoomAppService;
import com.bitdf.txing.oj.chat.service.cache.HotRoomCache;
import com.bitdf.txing.oj.chat.service.cache.RoomCache;
import com.bitdf.txing.oj.chat.service.cache.RoomFriendCache;
import com.bitdf.txing.oj.chat.service.cache.RoomGroupCache;
import com.bitdf.txing.oj.chat.service.strategy.AbstractMsghandler;
import com.bitdf.txing.oj.chat.service.strategy.MsgHandlerFactory;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.*;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserCache;
import com.bitdf.txing.oj.service.cache.UserRelateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    UserRelateCache userRelateCache;
    @Autowired
    GroupMemberService groupMemberService;
    @Autowired
    UserService userService;
    @Autowired
    RoomGroupService roomGroupService;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

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
//        Set<ZSetOperations.TypedTuple<String>> typedTuples = hotRoomCache.getRoomRange(hotStart, hotEnd);
//        List<Long> hotRoomIds = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull)
//                .map(Long::parseLong).collect(Collectors.toList());
//        roomIds.addAll(hotRoomIds);
        roomIds.add(1L);
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
                        chatRoomVO.setLastMessage(userMap.get(message.getFromUserId()).getUserName() + ":" + text);
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
    public Map<Long, Integer> getUnReadCountMap(Long userId, List<Long> roomIds) {
        List<Contact> contacts = contactService.getByRoomIds(roomIds, userId);
        return contacts.parallelStream().map(contact -> {
            Integer unReadCount = messageService.getUnReadCount(contact.getRoomId(), contact.getReadTime());
            return Pair.of(contact.getRoomId(), unReadCount);
        }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long userId) {
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
    public Map<Long, User> getFriendRoomMap(List<Long> friendRoomIds, Long userId) {
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

    public Double getCursorOrNull(String cursor) {
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

    /**
     * 获取群组详情
     *
     * @param userId
     * @param roomId
     * @return
     */
    @Override
    public GroupDetailVO getGroupDetail(Long userId, Long roomId) {
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Room room = roomCache.get(roomId);
        ThrowUtils.throwIf(roomGroup == null, "roomId错误");
        Long onlineCount;
        if (room.getHotFlag()) {
            onlineCount = userRelateCache.getOnlineCount();
        } else {
            List<Long> memberIdList = groupMemberService.getMemberListByGroupId(roomGroup.getId());
            onlineCount = userService.getGroupOnlineCount(memberIdList).longValue();
        }
        GroupRoleEnum groupRoleEnum = getGroupRole(userId, roomGroup, room);
        return GroupDetailVO.builder()
                .roomId(roomId)
                .onlineCount(onlineCount)
                .groupName(roomGroup.getName())
                .avatar(roomGroup.getAvatar())
                .role(groupRoleEnum.getType())
                .build();
    }

    public GroupRoleEnum getGroupRole(Long userId, RoomGroup roomGroup, Room room) {
        GroupMember member = groupMemberService.getMember(userId, roomGroup.getId());
        if (Objects.nonNull(member)) {
            return GroupRoleEnum.of(member.getRole());
        } else if (room.getHotFlag()) {
            return GroupRoleEnum.MEMBER;
        } else {
            return GroupRoleEnum.REMOVE;
        }
    }

    /**
     * @param groupMemberRequest
     * @return
     */
    @Override
    public CursorPageBaseVO<ChatMemberVO> getGroupMembersByCursor(GroupMemberRequest groupMemberRequest) {
        Room room = roomCache.get(groupMemberRequest.getRoomId());
        ThrowUtils.throwIf(room == null, "房间id不正确");
        List<Long> memberIdList;
        List<User> userList;
        Boolean isLast;
        String cursor;
        if (room.getHotFlag()) {
            memberIdList = null;
            CursorPageBaseVO<User> cursorPageBaseVO = userService.getMemberPageByCursor(memberIdList, groupMemberRequest);
            userList = cursorPageBaseVO.getList();
            isLast = cursorPageBaseVO.getIsLast();
            cursor = cursorPageBaseVO.getCursor();
        } else {
            RoomGroup roomGroup = roomGroupCache.get(room.getId());
            CursorPageBaseVO<GroupMember> cursorPageBaseVO = groupMemberService.getMembersPageByCursor(groupMemberRequest, roomGroup.getId());
            userList = cursorPageBaseVO.getList().stream().map(groupMember -> {
                User user = userCache.get(groupMember.getUserId());
                return user;
            }).collect(Collectors.toList());
            isLast = cursorPageBaseVO.getIsLast();
            cursor = cursorPageBaseVO.getCursor();
        }
        List<ChatMemberVO> chatMemberVOS = MemberAdapter.buildChatMember(userList);
        CursorPageBaseVO<ChatMemberVO> result = new CursorPageBaseVO<>(cursor, isLast, chatMemberVOS);
        return result;
    }

    /**
     * 创建群聊
     *
     * @param groupAddRequest
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Long addGroup(GroupAddRequest groupAddRequest, Long userId) {
        // 创建Room RoomGroup 添加群主
        RoomGroup roomGroup = cteateGroupRoom(userId, groupAddRequest);
        // 保存其他成员
        List<GroupMember> groupMembers = MemberAdapter.buildGroupMemberBatch(groupAddRequest.getUserIdList(), roomGroup.getId());
        boolean b = groupMemberService.saveBatch(groupMembers);
        // 发送通知 触发群聊每个成员的会话
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, userId));
        return roomGroup.getRoomId();
    }

    /**
     * 创建room
     *
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RoomGroup cteateGroupRoom(Long userId, GroupAddRequest groupAddRequest) {
        // TODO 这里可以作每个用户最大建群数的限制
        // 创建Room
        Room room = createRoom(RoomTypeEnum.GROUP);
        // 创建RoomGroup
        User user = userCache.get(userId);
        RoomGroup roomGroup = ChatAdapter.buildRoomGroup(user, room.getId(), groupAddRequest);
        roomGroupService.save(roomGroup);
        // 添加群主
        GroupMember groupMember = GroupMember.builder()
                .groupId(roomGroup.getId())
                .role(GroupRoleEnum.LEADER.getType())
                .userId(userId).build();
        groupMemberService.save(groupMember);
        return roomGroup;
    }

    public Room createRoom(RoomTypeEnum group) {
        Room room = ChatAdapter.buildRoom(group);
        roomService.save(room);
        return room;
    }

    @Override
    public void deleteFriendRoom(List<Long> asList) {
        ThrowUtils.throwIf(CollectionUtil.isEmpty(asList) || asList.size() != 2, "房间删除失败，用户参数数量不对");
        roomFriendService.disableRoom(ChatAdapter.sortUserIdList(asList));
    }
}
