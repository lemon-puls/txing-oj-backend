package com.bitdf.txing.oj.job.once;

import com.bitdf.txing.oj.esdao.PostEsDao;
import com.bitdf.txing.oj.model.dto.post.PostEsDTO;
import com.bitdf.txing.oj.model.entity.Post;
import com.bitdf.txing.oj.service.PostService;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 全量同步帖子到 es
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    @Override
    public void run(String... args) {
        List<Post> postList = postService.list();
        if (CollectionUtils.isEmpty(postList)) {
            return;
        }
//        List<PostEsDTO> postEsDTOList = postList.stream().map(PostEsDTO::objToDto).collect(Collectors.toList());
        List<PostEsDTO> postEsDTOList = postService.getPostEsDTOByPosts(postList);
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end, total {}", total);
    }
}
