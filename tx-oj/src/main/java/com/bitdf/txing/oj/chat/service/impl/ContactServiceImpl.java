package com.bitdf.txing.oj.chat.service.impl;

import com.bitdf.txing.oj.chat.mapper.ContactMapper;
import com.bitdf.txing.oj.chat.service.ContactService;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("contactService")
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<ContactEntity> page = this.page(
//                new Query<ContactEntity>().getPage(params),
//                new QueryWrapper<ContactEntity>()
//        );
//
//        return new PageUtils(page);
//    }

}
