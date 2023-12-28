package com.bitdf.txing.oj.chat.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitdf.txing.oj.chat.service.ContactService;


/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@RequestMapping("chat/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
//        PageUtils page = contactService.queryPage(params);
//
//        return R.ok().put("page", page);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        Contact contact = contactService.getById(id);

        return R.ok().put("contact", contact);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Contact contact) {
        contactService.save(contact);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Contact contact) {
        contactService.updateById(contact);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        contactService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
