package com.bitdf.txing.oj.chat.service.impl;

import com.bitdf.txing.oj.chat.mapper.MessageMapper;
import com.bitdf.txing.oj.model.entity.chat.Message;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.service.MessageService;


@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MessageEntity> page = this.page(
//                new Query<MessageEntity>().getPage(params),
//                new QueryWrapper<MessageEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}
