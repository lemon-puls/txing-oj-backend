package com.bitdf.txing.oj.chat.consumer;

import com.bitdf.txing.oj.chat.domain.enume.RoomTypeEnum;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.service.ContactService;
import com.bitdf.txing.oj.chat.service.MessageService;
import com.bitdf.txing.oj.chat.service.RoomFriendService;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.adapter.WsAdapter;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.chat.service.business.PushService;
import com.bitdf.txing.oj.chat.service.cache.GroupMemberCache;
import com.bitdf.txing.oj.chat.service.cache.HotRoomCache;
import com.bitdf.txing.oj.chat.service.cache.RoomCache;
import com.bitdf.txing.oj.judge.JudgeService;
import com.bitdf.txing.oj.model.entity.chat.GroupMember;
import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Lizhiwei
 * @date 2023/12/6 17:06:15
 * 注释：
 */
@Service
@RabbitListener(queues = {"message.send.queue"})
@Slf4j
public class MessageSendMqListener {

    @Autowired
    MessageService messageService;
    @Autowired
    RoomCache roomCache;
    @Autowired
    ChatService chatService;
    @Autowired
    RoomService roomService;
    @Autowired
    HotRoomCache hotRoomCache;
    @Autowired
    PushService pushService;
    @Autowired
    GroupMemberCache groupMemberCache;
    @Autowired
    RoomFriendService roomFriendService;
    @Autowired
    ContactService contactService;

    /**
     * 该监听器负责处理队列里的判题请求
     *
     * @param msgId
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void listener(Long msgId, Channel channel, Message message) throws IOException {
        try {
            log.info("[监听消息发送队列]收到消息： {}", msgId);
            com.bitdf.txing.oj.model.entity.chat.Message messageEntity = messageService.getById(msgId);
            Room room = roomCache.get(messageEntity.getRoomId());
            ChatMessageVO messageVO = chatService.getMessageVO(msgId, null);
            // 更新房间的最新消息及时间
            roomService.refreshActiveMsgAndTime(room.getId(), messageEntity.getId(), messageEntity.getCreateTime());
            // 删除缓存中的room
            roomCache.delete(room.getId());
            if (room.getHotFlag()) {
                // 更新热门聊天房间的活跃时间
                hotRoomCache.refreshActiveTime(room.getId(), messageEntity.getCreateTime());
                // 推送给所有用户
                WsBaseVO<ChatMessageVO> wsBaseVO = WsAdapter.buildMsgSend(messageVO);
                pushService.sendPushMsg(wsBaseVO, messageEntity.getId());
            } else {
                List<Long> targetUserIds = new ArrayList<>();
                if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getCode())) {
                    // 群聊
                    targetUserIds = groupMemberCache.getMemberUserIdList(room.getId());
                } if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getCode())) {
                    // 私聊
                    RoomFriend roomFriend = roomFriendService.getByRoomId(room.getId());
                    targetUserIds = Arrays.asList(roomFriend.getUserId1(), roomFriend.getUserId2());
                }
                // 更新或创建所有目标用户的会话时间
                contactService.updateOrCreateActiveTime(room.getId(), targetUserIds, messageEntity.getId(), messageEntity.getCreateTime());
                // 推送
                pushService.sendPushMsg(WsAdapter.buildMsgSend(messageVO), targetUserIds, messageEntity.getId());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("完成消息{}的处理并且发送到Ws推送队列", msgId);
        } catch (Exception e) {
            log.info("[消息处理失败]：{}", msgId);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
