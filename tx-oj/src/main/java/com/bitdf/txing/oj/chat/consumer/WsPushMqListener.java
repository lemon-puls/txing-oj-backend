package com.bitdf.txing.oj.chat.consumer;

import com.bitdf.txing.oj.chat.constant.ChatMqConstant;
import com.bitdf.txing.oj.chat.domain.dto.PushMsgMqDTO;
import com.bitdf.txing.oj.chat.domain.vo.response.WsBaseVO;
import com.bitdf.txing.oj.chat.enume.WsPushTypeEnum;
import com.bitdf.txing.oj.chat.enume.WsRespTypeEnum;
import com.bitdf.txing.oj.chat.service.business.WebSocketService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Lizhiwei
 * @date 2023/12/6 17:06:15
 * 注释：
 */
@Service
@RabbitListener(queues = {ChatMqConstant.WEBSOCKET_PUSH_QUEUE})
@Slf4j
public class WsPushMqListener {

    @Autowired
    WebSocketService webSocketService;

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
            WsBaseVO<?> wsBaseVO = pushMsgMqDTO.getWsBaseVO();
            log.info("[推送队列收到消息]： {}", wsBaseVO.getData());
            // TODO 开始推送
            WsPushTypeEnum pushTypeEnum = WsPushTypeEnum.of(pushMsgMqDTO.getType());
            switch (pushTypeEnum) {
                case USER:
                    pushMsgMqDTO.getUserIds().forEach(userId -> {
                        webSocketService.sendToUserId(wsBaseVO, userId);
                    });
                    break;
                case ALL:
                    Long excludeUserId = null;
                    if (wsBaseVO.getType() == WsRespTypeEnum.MESSAGE.getType()) {
                        excludeUserId = pushMsgMqDTO.getExcludeUserId();
                    }
                    webSocketService.sendToAllOnline(wsBaseVO, excludeUserId);
                    break;
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("完成消息的推送，确认消息");
        } catch (Exception e) {
            log.info("[消息推送失败]");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
