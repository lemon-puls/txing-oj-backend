package com.bitdf.txing.oj.manager;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Cos 操作测试
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@SpringBootTest
class CosManagerTest {

    @Resource
    private CosManager cosManager;

    @Test
    void putObject() {
        cosManager.putObject("test", "test.json");
    }

    @Test
    void deleteObject() {
//        cosManager.deleteOject("/post_content_img/1726766580186198017/SOtmNwGH-42f3f796a326707a796ec644af28e1a1.jpg");
        List<String> strings = new ArrayList<>();
        strings.add("post_content_img/1726766580186198017/BxuEFbY2-cb1cd260f70f3e7f479c07775047905.png");
        cosManager.deleteOjects(strings);
    }

}