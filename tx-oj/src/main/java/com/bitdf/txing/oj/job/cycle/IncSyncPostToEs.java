package com.bitdf.txing.oj.job.cycle;

import com.bitdf.txing.oj.esdao.PostEsDao;
import com.bitdf.txing.oj.mapper.PostMapper;
import com.bitdf.txing.oj.model.dto.post.PostEsDTO;
import com.bitdf.txing.oj.model.entity.Post;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.bitdf.txing.oj.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 增量同步帖子到 es
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncPostToEs {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostEsDao postEsDao;
    @Autowired
    PostService postService;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        List<Post> postList = postMapper.listPostWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(postList)) {
            log.info("no inc post");
            return;
        }
//        List<PostEsDTO> postEsDTOList = postList.stream()
//                .map(PostEsDTO::objToDto)
//                .collect(Collectors.toList());
        List<PostEsDTO> postEsDTOList = postService.getPostEsDTOByPosts(postList);
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("IncSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("IncSyncPostToEs end, total {}", total);
    }
}
