package com.bitdf.txing.oj.config;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bitdf.txing.oj.model.entity.QuestionSubmit;
import com.bitdf.txing.oj.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;


@Configuration
@Slf4j
public class MyRabbitMQConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    QuestionSubmitService questionSubmitService;

    /**
     * 定制RabbitTemplate
     * 1、服务收到消息就会回调
     * 1、spring.rabbitmq.publisher-confirms: true
     * 2、设置确认回调
     * 2、消息正确抵达队列就会进行回调
     * 1、spring.rabbitmq.publisher-returns: true
     * spring.rabbitmq.template.mandatory: true
     * 2、设置确认回调ReturnCallback
     * <p>
     * 3、消费端确认(保证每个消息都被正确消费，此时才可以broker删除这个消息)
     */
    @PostConstruct  //MyRabbitConfig对象创建完成以后，执行这个方法
    public void initRabbitTemplate() {

        /**
         * 1、只要消息抵达Broker就ack=true
         * correlationData：当前消息的唯一关联数据(这个是消息的唯一id)
         * ack：消息是否成功收到
         * cause：失败的原因
         */
        //设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.info("作答提交[{}]: 发送到Mq交换机失败 {}", correlationData.getId(), cause);
                boolean update = questionSubmitService.update(new UpdateWrapper<QuestionSubmit>().lambda()
                        .eq(QuestionSubmit::getId, correlationData.getId())
                        .eq(QuestionSubmit::getStatus, 0)
                        .setSql("status = 4"));
            }
        });


        /**
         * 只要消息没有投递给指定的队列，就触发这个失败回调
         * message：投递失败的消息详细信息
         * replyCode：回复的状态码
         * replyText：回复的文本内容
         * exchange：当时这个消息发给哪个交换机
         * routingKey：当时这个消息用哪个路邮键
         */
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String submitId = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("作答提交[{}]: 路由到Mq队列失败 {}", submitId, replyText);
            boolean update = questionSubmitService.update(new UpdateWrapper<QuestionSubmit>().lambda()
                    .eq(QuestionSubmit::getId, submitId)
                    .eq(QuestionSubmit::getStatus, 0)
                    .setSql("status = 4"));
        });

//        rabbitTemplate.setRecoveryCallback();
    }

}
