package com.bitdf.txing.oj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author Lizhiwei
 * @date 2022/9/6 18:55:59
 * 注释：
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
//        return new GenericFastJsonRedisSerializer();
//        return new StringRedisSerializer();
        return new GenericJackson2JsonRedisSerializer();

    }
}
