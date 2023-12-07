package com.bitdf.txing.oj.job.cycle;

import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.manager.CosManager;
import com.bitdf.txing.oj.model.dto.post.PostEsDTO;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.service.PostService;
import com.bitdf.txing.oj.utils.CustomStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lizhiwei
 * @date 2023/12/5 22:59:45
 * 注释：
 */
@Component
@Slf4j
public class DeleteImgFromOss {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    CosManager cosManager;
    @Autowired
    PostService postService;

    /**
     * 每天凌晨两点执行 删除废弃的图片
     */
//    @Scheduled(fixedRate = 60 * 1000)
    @Scheduled(cron = "0 0 2 * * ?")
    public void run() {

        // 删除oj:post:content:imgs:add中所有过期图片
        clearExpireImgs();

        clearExpireImgsByPost();
    }

    public void clearExpireImgs() {
        // 删除oj:post:content:imgs:add中所有过期图片
        BoundHashOperations<String, String, String> imgsHashOps = stringRedisTemplate.boundHashOps(RedisKeyConstant.POST_CONTENT_IMGS_ADD);
        Map<String, String> entries = imgsHashOps.entries();
        // 收集需要删除的图片
        List<String> needDeleteImgUrls = new ArrayList<>();
        List<String> needDeleteImgKeys = new ArrayList<>();
        entries.forEach((k, v) -> {
            Long timeStamp = Long.valueOf(v);
            long currentTimestamp = System.currentTimeMillis(); // 当前时间戳，单位为毫秒
            long timeDifference = currentTimestamp - timeStamp; // 计算时间差，单位为毫秒
            long hoursInDay = 1000 * 60 * 5; // 24小时对应的毫秒数
            // 1000 * 60 * 5; // 5分钟 24L * 60L * 60L * 1000L
            if (timeDifference > hoursInDay) {
                // 超过了24小时
                needDeleteImgUrls.add(k);
                String prefix = "myqcloud.com/";
                String key = k.substring(k.indexOf(prefix) + prefix.length());
                needDeleteImgKeys.add(key);
            }
        });
        log.info("需要删除的图片：{}", needDeleteImgUrls);
        if (!needDeleteImgKeys.isEmpty() && !needDeleteImgUrls.isEmpty()) {
            // 删除oss中图片
            cosManager.deleteOjects(needDeleteImgKeys);
            // 删除Redis中记录
            Long delete = imgsHashOps.delete(needDeleteImgUrls.toArray());
        }
    }

    public void clearExpireImgsByPost() {
        stringRedisTemplate.execute((RedisCallback) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match("oj:post:content:imgs:update:*")
                    .count(10000).build())) {
                while (cursor.hasNext()) {
                    String s = new String(cursor.next(), "Utf-8");
                    String postId = s.substring(s.lastIndexOf(":") + 1);
                    Post post = postService.getById(postId);
                    if (post == null) {
                        Boolean delete = stringRedisTemplate.delete(s);
                        continue;
                    }
                    List<String> collect = CustomStringUtils.getImgUrlsFromMd(post.getContent(), "https", true);
                    BoundHashOperations<String, String, String> imgsHashOps =
                            stringRedisTemplate.boundHashOps(RedisKeyConstant.POST_CONTENT_IMGS_UPDATE + postId);
                    Map<String, String> entries = imgsHashOps.entries();
                    // 收集需要删除的图片
                    List<String> needDeleteImgUrls = new ArrayList<>();
                    List<String> needDeleteImgKeys = new ArrayList<>();
                    entries.forEach((k, v) -> {
                        Long timeStamp = Long.valueOf(v);
                        long currentTimestamp = System.currentTimeMillis(); // 当前时间戳，单位为毫秒
                        long timeDifference = currentTimestamp - timeStamp; // 计算时间差，单位为毫秒
                        long hoursInDay = 1000 * 60 * 5; // 24小时对应的毫秒数
                        // 1000 * 60 * 5; // 5分钟  24L * 60L * 60L * 1000L
                        if (timeDifference > hoursInDay) {
                            // 超过了24小时
                            needDeleteImgUrls.add(k);
                            if (!collect.contains(k)) {
                                // 需要删除
                                String prefix = "myqcloud.com/";
                                String key = k.substring(k.indexOf(prefix) + prefix.length());
                                needDeleteImgKeys.add(key);
                            }
                        }
                    });
                    if (!needDeleteImgKeys.isEmpty()) {
                        // 删除oss中图片
                        cosManager.deleteOjects(needDeleteImgKeys);
                    }
                    if (!needDeleteImgUrls.isEmpty()) {
                        // 删除Redis中记录
                        Long delete = imgsHashOps.delete(needDeleteImgUrls.toArray());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
