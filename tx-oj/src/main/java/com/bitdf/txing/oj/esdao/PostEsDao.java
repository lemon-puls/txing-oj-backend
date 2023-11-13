package com.bitdf.txing.oj.esdao;

import com.bitdf.txing.oj.model.dto.post.PostEsDTO;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    List<PostEsDTO> findByUserId(Long userId);
}