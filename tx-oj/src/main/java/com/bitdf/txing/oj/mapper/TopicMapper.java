package com.bitdf.txing.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitdf.txing.oj.common.PageRequest;
import com.bitdf.txing.oj.model.entity.forum.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 
 * @author lizhiwei
 * @email 
 * @date 2024-03-27 12:59:55
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    Page<Topic> getUserFavourPage(Page<Topic> page, @Param("userId") Long userId, @Param("pageRequest") PageRequest pageRequest);
}
