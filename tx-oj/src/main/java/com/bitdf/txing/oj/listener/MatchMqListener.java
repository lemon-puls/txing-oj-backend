package com.bitdf.txing.oj.listener;

import com.bitdf.txing.oj.config.MyMqConfig;
import com.bitdf.txing.oj.model.dto.match.MatchSubmitBatchRequest;
import com.bitdf.txing.oj.service.MatchOnlinepkAppService;
import com.bitdf.txing.oj.service.business.MatchAppService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MatchMqListener {
    @Autowired
    MatchAppService matchAppService;
    @Autowired
    MatchOnlinepkAppService matchOnlinepkAppService;

    @RabbitListener(queues = "match.handle.queue.prod")
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

    @RabbitListener(queues = MyMqConfig.Match_WEEK_CHECK_QUEUE)
    @RabbitHandler
    public void matchHandleListener(Long matchId, Channel channel, Message message) throws IOException {
        log.info("收到周赛比赛Id： {}, 开始处理", matchId);
        try {
            boolean b = matchAppService.buildMatchResult(matchId);
            if (!b) {
                Thread.sleep(1000 * 10);
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitListener(queues = MyMqConfig.Match_PK_CHECK_QUEUE)
    @RabbitHandler
    public void matchPkHandleListener(Long matchId, Channel channel, Message message) throws IOException {
        log.info("收到PK比赛Id： {}, 开始处理", matchId);
        try {
            boolean b = matchOnlinepkAppService.checkAndBuildPkMatchResult(matchId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
