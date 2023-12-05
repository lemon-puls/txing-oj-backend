package com.bitdf.txing.oj.manager;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
        cosManager.deleteOject("/post_cover/1/fhKvUpJY-42f3f796a326707a796ec644af28e1a1.jpg");
    }

}