package com.bitdf.txing.oj;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.chat.service.cache.GroupMemberCache;
import com.bitdf.txing.oj.model.dto.question.JudgeConfig;
import com.bitdf.txing.oj.model.entity.user.User;
import com.google.gson.Gson;
import com.lemon.util.service.TableGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest
public class CommonTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    TableGenerator tableGenerator;

    @Test
    void createTable() throws SQLException {
        tableGenerator.generateTable();
    }

    @Autowired
    GroupMemberCache groupMemberCache;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void testRabbitMq() {
        User user = new User();
        user.setUserName("nice明天会更好");
        rabbitTemplate.convertAndSend("no.exist", "hehe", user, new CorrelationData(UUID.randomUUID().toString()));
    }

    @Test
    void testCache() {
//        for (int i = 0; i < 5; i++) {
//            System.out.println(groupMemberCache.getRoomMemberCount(2L));
//        }
    }

    @Test
    void toJSON() {
        JudgeConfig judgeConfig = new JudgeConfig();
        judgeConfig.setMemoryLimit(1000L);
        judgeConfig.setTimeLimit(1000L);
        String s = JSONUtil.toJsonStr(judgeConfig);
        System.out.println(s);
        Gson GSON = new Gson();
        String s1 = GSON.toJson(judgeConfig);
        System.out.println(s1);
        String s2 = JSON.toJSONString(judgeConfig);
        System.out.println(s2);
    }

    @Test
    void findImgsTest() {
        String text = "<替换为文章内容进行测试>";

        // 定义正则表达式
//        String regex = "!\\[[^\\]]*\\]\\((.*?)\"";
        String regex = "!\\[[^\\]]*\\]\\((.*?)(?=\\s)";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象，用于匹配文本中的内容
        Matcher matcher = pattern.matcher(text);

        // 创建列表，用于存储匹配到的链接
        List<String> links = new ArrayList<>();

        // 遍历匹配结果
        while (matcher.find()) {
            // 获取匹配到的链接
            String link = matcher.group();
            links.add(link);
        }
    }

    @Test
    void redisTest() {
        Set<String> keys = new HashSet<>();

        stringRedisTemplate.execute((RedisCallback) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match("oj:*")
                    .count(10000).build())) {
                while (cursor.hasNext()) {
                    String s = new String(cursor.next(), "Utf-8");
                    String substring = s.substring(s.lastIndexOf(":"));
                    keys.add(s);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
