package com.bitdf.txing.oj.chat.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatRoomVO;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.chat.service.business.RoomAppService;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.Contact;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.chat.service.ContactService;


/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@Api(tags = "聊天会话相关接口")
@RequestMapping("chat/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomAppService roomAppService;
    @Autowired
    ChatService chatService;

    /**
     * 会话分页查询（游标翻页）
     */
    @PostMapping("/list")
    @ApiOperation("查询（游标翻页）")
    @AuthCheck(mustRole = "login")
    public R list(@RequestBody CursorPageBaseRequest cursorPageBaseRequest) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        CursorPageBaseVO<ChatRoomVO> cursorPageBaseVO = roomAppService.getContactPageByCursor(cursorPageBaseRequest, userId);
        return R.ok(cursorPageBaseVO);
    }


    @GetMapping("detail/get")
    @ApiOperation("获取会话详情")
    @AuthCheck(mustRole = "login")
    public R getDetailByRoomId(@RequestParam("roomId") Long roomId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        ChatRoomVO chatRoomVO = roomAppService.getContactDetailByRoomId(roomId, userId);
        return R.ok(chatRoomVO);
    }

    @GetMapping("detail/friend/get")
    @ApiOperation("获取会话详情（by FriendId）")
    @AuthCheck(mustRole = "login")
    public R getDetailByFriendId(@RequestParam("friendId") Long friendId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        ChatRoomVO chatRoomVO = roomAppService.getContactDetailByFriendId(userId, friendId);
        return R.ok(chatRoomVO);
    }


    @PostMapping("/msg/read")
    @ApiOperation("消息阅读上报")
    @AuthCheck(mustRole = "login")
    public R msgReadReport(@RequestParam("roomId") @ApiParam("房间id") Long roomId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        chatService.msgRead(userId, roomId);
        return R.ok();
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
