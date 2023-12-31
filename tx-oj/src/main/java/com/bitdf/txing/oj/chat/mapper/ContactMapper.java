package com.bitdf.txing.oj.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@Mapper
public interface ContactMapper extends BaseMapper<Contact> {

    void updateOrCreateActiveTime(@Param("roomId") Long roomId, @Param("targetUserIds") List<Long> targetUserIds,
                                  @Param("msgId") Long msgId, @Param("createTime") Date createTime);
}
