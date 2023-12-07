package com.bitdf.txing.oj.listener;

import com.bitdf.txing.oj.judge.JudgeService;
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
@RabbitListener(queues = {"waitting.judge.queue"})
@Slf4j
public class JudgeMqListener {

    @Autowired
    JudgeService judgeService;

    /**
     * 该监听器负责处理队列里的判题请求
     *
     * @param submitId
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void listener(Long submitId, Channel channel, Message message) throws IOException {
        log.info("收到提交请求： {}, 开始执行判题", submitId);
        try {
            // 开始执行请求
            judgeService.doJudge(submitId);
            log.info("完成判题，确认消息");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.info("执行判题出错，重放消息");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
