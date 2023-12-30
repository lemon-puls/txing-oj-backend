package com.bitdf.txing.oj.chat.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.chat.domain.vo.request.ChatMessageRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.chat.service.MessageService;



/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@RequestMapping("chat/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ChatService chatService;

    @PostMapping("/msg")
    @ApiOperation("发送消息")
    @AuthCheck(mustRole = "login")
    public R sendMsg(@RequestBody ChatMessageRequest request) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        Long msgId = chatService.sendMsg(request, userId);
        ChatMessageVO chatMessageVO = chatService.getMessageVO(msgId, userId);
        return R.ok(chatMessageVO);
    }

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
////        PageUtils page = messageService.queryPage(params);
////
////        return R.ok().put("page", page);
//        return R.ok();
//    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id){
//		Message message = messageService.getById(id);
//
//        return R.ok().put("message", message);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody Message message){
//		messageService.save(message);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody Message message){
//		messageService.updateById(message);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//		messageService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
