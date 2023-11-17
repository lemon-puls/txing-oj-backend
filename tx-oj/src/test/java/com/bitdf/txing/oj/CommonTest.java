package com.bitdf.txing.oj;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.model.dto.question.JudgeConfig;
import com.google.gson.Gson;
import com.lemon.util.service.TableGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
public class CommonTest {
    @Autowired
    TableGenerator tableGenerator;

    @Test
    void createTable() throws SQLException {
        tableGenerator.generateTable();
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
}
