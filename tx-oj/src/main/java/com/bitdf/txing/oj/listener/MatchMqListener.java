package com.bitdf.txing.oj.listener;

import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.service.business.MatchAppService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
@RabbitListener(queues = "match.handle.queue.dev")
@Slf4j
@Service
public class MatchMqListener {
    @Autowired
    MatchAppService matchAppService;
    @RabbitHandler
    public void matchHandleListener(MatchSubmitBatchRequest request, Channel channel, Message message) throws IOException {
        log.info("收到比赛作答提交： {}, 开始处理", request);
        try {
            matchAppService.submitAll(request);

            log.info("完成判题，确认消息");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.info("执行判题出错，重放消息");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
