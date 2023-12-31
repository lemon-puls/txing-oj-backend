package com.bitdf.txing.oj.chat.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatRoomVO;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.business.RoomAppService;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.chat.service.ContactService;


/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@Api("聊天会话相关接口")
@RequestMapping("chat/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomAppService roomAppService;

    /**
     * 会话分页查询（游标翻页）
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = "login")
    public R list(@RequestBody CursorPageBaseRequest cursorPageBaseRequest) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        CursorPageBaseVO<ChatRoomVO> cursorPageBaseVO = roomAppService.getContactPageByCursor(cursorPageBaseRequest, userId);
        return R.ok(cursorPageBaseVO);
    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id) {
//        Contact contact = contactService.getById(id);
//
//        return R.ok().put("contact", contact);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody Contact contact) {
//        contactService.save(contact);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody Contact contact) {
//        contactService.updateById(contact);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids) {
//        contactService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
