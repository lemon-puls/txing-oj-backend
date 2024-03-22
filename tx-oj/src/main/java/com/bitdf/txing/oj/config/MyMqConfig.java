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

    //    @Value("${spring.profiles.active}")
    private static String profile = "dev";

    public static final String JUDGE_EXCHANGE = genName("judge.exchange");
    public static final String WAITTING_JUDGE_QUEUE = genName("waitting.judge.queue");
    public static final String WAITTING_JUDGE_ROUTINGKEY = genName("submit.and.judge");

    public static final String MATCH_HANDLE_QUEUE = genName("match.handle.queue");
    public static final String MATCH_HANDLE_ROUTTINGKEY = genName("match.and.handle");

    public static final String WEBSOCKET_PUSH_QUEUE = "websocket_push_queue.dev";


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 交换机--判题
     *
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
        return new TopicExchange(JUDGE_EXCHANGE, true, false);

    }

    /**
     * 用户作答提交在此队列等待处理（判题）
     *
     * @return
     */
    @Bean("waitting.judge.queue")
    public Queue orderSecKillOrrderQueue() {
        Queue queue = new Queue(WAITTING_JUDGE_QUEUE, true, false, false);
        return queue;
    }

    /**
     * 绑定--判题
     *
     * @return
     */
    @Bean
    public Binding judgeBinding() {
        Binding binding = new Binding(
                WAITTING_JUDGE_QUEUE,
                Binding.DestinationType.QUEUE,
                JUDGE_EXCHANGE,
                WAITTING_JUDGE_ROUTINGKEY,
                null);
        return binding;
    }

    // 比赛 ===================================================

    /**
     * 周赛作答等待处理队列
     *
     * @return
     */
    @Bean("waitting.week.match.handle.queue")
    public Queue waittingWeekMatchHandleQueue() {
        Queue queue = new Queue(MATCH_HANDLE_QUEUE, true, false, false);
        return queue;
    }

    /**
     * 绑定--等待处理比赛作答
     *
     * @return
     */
    @Bean
    public Binding matchHandleBinding() {
        Binding binding = new Binding(
                MATCH_HANDLE_QUEUE,
                Binding.DestinationType.QUEUE,
                JUDGE_EXCHANGE,
                MATCH_HANDLE_ROUTTINGKEY,
                null);
        return binding;
    }

    //  聊天=====================================================

    /**
     * 聊天交换机
     *
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
     * 消息发送队列
     *
     * @return
     */
    @Bean("message.send.queue")
    public Queue messageSendQueue() {
        Queue queue = new Queue(ChatMqConstant.MESSAGE_SEND_QUEUE, true, false, false);
        return queue;
    }

    /**
     * 消息发送队列 === 聊天交换机
     *
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
     * websocket推送队列
     *
     * @return
     */
    @Bean("websocket.push.queue")
    public Queue websocketPushQueue() {
        Queue queue = new Queue(WEBSOCKET_PUSH_QUEUE, true, false, false);
        return queue;
    }

    /**
     * websocket推送队列 === 聊天交换机
     *
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


    public static String genName(String base) {
        return base + "." + profile;
    }
}
