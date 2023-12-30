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
}
