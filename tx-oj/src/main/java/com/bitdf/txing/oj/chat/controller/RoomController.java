package com.bitdf.txing.oj.chat.controller;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupAddRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupMemberRemoveRequest;
import com.bitdf.txing.oj.chat.domain.vo.request.GroupMemberRequest;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMemberVO;
import com.bitdf.txing.oj.chat.domain.vo.response.GroupDetailVO;
import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.business.RoomAppService;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@RequestMapping("chat/room")
@Api(tags = "聊天房间相关接口")
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomAppService roomAppService;

    @GetMapping("/group/detail/get")
    @ApiOperation("群组详情")
    @AuthCheck(mustRole = "login")
    public R groupDetail(@RequestParam("roomId") Long roomId) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        GroupDetailVO groupDetailVO = roomAppService.getGroupDetail(userId, roomId);
        return R.ok(groupDetailVO);
    }

    @PostMapping("/group/member/page")
    @ApiOperation("查询群组成员（游标）")
    @AuthCheck(mustRole = "login")
    public R getMembersByCursor(@RequestBody GroupMemberRequest groupMemberRequest) {
        CursorPageBaseVO<ChatMemberVO> cursorPageBaseVO = roomAppService.getGroupMembersByCursor(groupMemberRequest);
        return R.ok(cursorPageBaseVO);
    }

    @PostMapping("/group/add")
    @ApiOperation("创建群聊")
    @AuthCheck(mustRole = "login")
    public R addGroup(@RequestBody GroupAddRequest groupAddRequest) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        Long roomId = roomAppService.addGroup(groupAddRequest, userId);
        return R.ok(roomId);
    }

    /**
     * 移除群聊成员
     * @param groupMemberRemoveRequest
     * @return
     */
    @PostMapping("/group/member/remove")
    @AuthCheck(mustRole = "login")
    public R removeGroupMember(@RequestBody GroupMemberRemoveRequest groupMemberRemoveRequest) {
        if (Objects.isNull(groupMemberRemoveRequest.getUserId())) {
            User user = AuthInterceptor.userThreadLocal.get();
            groupMemberRemoveRequest.setUserId(user.getId());
        }
        roomAppService.removeGroupMember(groupMemberRemoveRequest);
        return R.ok();
    }


    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params) {
////        PageUtils page = roomService.queryPage(params);
////
////        return R.ok().put("page", page);
//        return R.ok();
//    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id) {
//        Room room = roomService.getById(id);
//
//        return R.ok().put("room", room);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody Room room) {
//        roomService.save(room);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody Room room) {
//        roomService.updateById(room);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids) {
//        roomService.removeByIds(Arrays.asList(ids));
//        return R.ok();
//    }

}
