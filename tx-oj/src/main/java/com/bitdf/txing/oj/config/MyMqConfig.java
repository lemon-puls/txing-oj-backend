package com.bitdf.txing.oj.config;

import com.bitdf.txing.oj.chat.constant.ChatMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lizhiwei
 * @date 2023/9/25 21:26:35
 * 注释：
 */
@Configuration
public class MyMqConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 交换机--判题
     * @return
     */
    @Bean
    public Exchange judgeExchange() {
        /*
         *   String name,
         *   boolean durable,
         *   boolean autoDelete,
         *   Map<String, Object> arguments
         * */
        return new TopicExchange("judge.exchange", true, false);

    }

    /**
     * 用户作答提交在此队列等待处理（判题）
     * @return
     */
    @Bean("waitting.judge.queue")
    public Queue orderSecKillOrrderQueue() {
        Queue queue = new Queue("waitting.judge.queue", true, false, false);
        return queue;
    }

    /**
     * 绑定--判题
     * @return
     */
    @Bean
    public Binding judgeBinding() {
        Binding binding = new Binding(
                "waitting.judge.queue",
                Binding.DestinationType.QUEUE,
                "judge.exchange",
                "submit.and.judge",
                null);
        return binding;
    }

    //  聊天=====================================================

    /**
     * 聊天交换机
     * @return
     */
    @Bean
    public Exchange chatExchange() {
        /*
         *   String name,
         *   boolean durable,
         *   boolean autoDelete,
         *   Map<String, Object> arguments
         * */
        return new TopicExchange(ChatMqConstant.CHAT_EXCHANGE, true, false);

    }

    /**
     *  消息发送队列
     * @return
     */
    @Bean("message.send.queue")
    public Queue messageSendQueue() {
        Queue queue = new Queue(ChatMqConstant.MESSAGE_SEND_QUEUE, true, false, false);
        return queue;
    }

    /**
     * 消息发送队列 === 聊天交换机
     * @return
     */
    @Bean
    public Binding messageSendBinding() {
        Binding binding = new Binding(
                ChatMqConstant.MESSAGE_SEND_QUEUE,
                Binding.DestinationType.QUEUE,
                ChatMqConstant.CHAT_EXCHANGE,
                ChatMqConstant.MESSAGE_SEND_ROUTING_KEY,
                null);
        return binding;
    }

    /**
     *  websocket推送队列
     * @return
     */
    @Bean("websocket.push.queue")
    public Queue websocketPushQueue() {
        Queue queue = new Queue(ChatMqConstant.WEBSOCKET_PUSH_QUEUE, true, false, false);
        return queue;
    }

    /**
     * websocket推送队列 === 聊天交换机
     * @return
     */
    @Bean
    public Binding websocketPushBinding() {
        Binding binding = new Binding(
                ChatMqConstant.WEBSOCKET_PUSH_QUEUE,
                Binding.DestinationType.QUEUE,
                ChatMqConstant.CHAT_EXCHANGE,
                ChatMqConstant.WEBSOCKET_PUSH_ROUTING_KEY,
                null);
        return binding;
    }



}
