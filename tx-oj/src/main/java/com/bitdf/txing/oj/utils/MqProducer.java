package com.bitdf.txing.oj.utils;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Lizhiwei
 * @date 2023/12/30 19:13:05
 * 注释：
 */
@Component
public class MqProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param exchange    目的交换机
     * @param routingKey  路由键
     * @param messageBody 消息
     * @param id          消息唯一标识
     */
    public void sendMsg(String exchange, String routingKey, Object messageBody, String id) {
        rabbitTemplate.convertAndSend(exchange, routingKey,
                messageBody, new CorrelationData(id));
    }

    /**
     * 发送消息
     *
     * @param exchange    目的交换机
     * @param routingKey  路由键
     * @param messageBody 消息
     */
    public void sendMsg(String exchange, String routingKey, Object messageBody) {
        rabbitTemplate.convertAndSend(exchange, routingKey, messageBody);
    }

    /**
     * 发送消息 并且指定延时时间 适用于延时交换机（使用延时插件实现的延时）
     *
     * @param delay 单位：毫秒
     */
    public void sendMsgWithDelay(String exchange, String routingKey, Object messageBody, Long delay) {
        rabbitTemplate.convertAndSend(exchange, routingKey, messageBody, correlationData -> {
            correlationData.getMessageProperties().setDelay(delay.intValue());
            return correlationData;
        });
    }
}
