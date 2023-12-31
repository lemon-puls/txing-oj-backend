package com.bitdf.txing.oj.chat.consumer;

import com.bitdf.txing.oj.chat.constant.ChatMqConstant;
import com.bitdf.txing.oj.chat.domain.dto.PushMsgMqDTO;
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
@RabbitListener(queues = {ChatMqConstant.WEBSOCKET_PUSH_QUEUE})
@Slf4j
public class WsPushMqListener {


    /**
     * 该监听器负责处理队列里的判题请求
     *
     * @param pushMsgMqDTO
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void listener(PushMsgMqDTO pushMsgMqDTO, Channel channel, Message message) throws IOException {
        try {
            log.info("[推送队列收到消息]： {}", pushMsgMqDTO.getWsBaseVO().getData());
            // TODO 开始推送
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("完成消息的推送，确认消息");
        } catch (Exception e) {
            log.info("[消息推送失败]");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
