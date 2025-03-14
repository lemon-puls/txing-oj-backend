package com.bitdf.txing.oj.controller;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.user.FriendVO;
import com.bitdf.txing.oj.service.UserFriendService;
import com.bitdf.txing.oj.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 23:46:51
 */
@RestController
@RequestMapping("user/friend")
public class UserFriendController {
    @Autowired
    private UserFriendService userFriendService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation("查询联系人列表（游标翻页）")
    @AuthCheck(mustRole = "login")
    public R list(@RequestBody CursorPageBaseRequest cursorPageBaseRequest){
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        CursorPageBaseVO<FriendVO> response = userFriendService.cursorPage(userId, cursorPageBaseRequest);
        return R.ok(response);
    }
    /**
     * 删除好友
     */
    @AuthCheck(mustRole = "login")
    @GetMapping("/delete")
    public R delete(@RequestParam("friendId") Long friendId){
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        userFriendService.deleteFriend(userId, friendId);
        return R.ok();
    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id){
//		UserFriend userFriend = userFriendService.getById(id);
//
//        return R.ok().put("userFriend", userFriend);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody UserFriend userFriend){
//		userFriendService.save(userFriend);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody UserFriend userFriend){
//		userFriendService.updateById(userFriend);
//
//        return R.ok();
//    }


}
