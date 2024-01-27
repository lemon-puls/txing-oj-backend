package com.bitdf.txing.oj.controller;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.chat.domain.vo.response.ChatMessageVO;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.user.FriendApplyVO;
import com.bitdf.txing.oj.service.UserApplyService;
import com.bitdf.txing.oj.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 23:46:51
 */
@RestController
@RequestMapping("user/apply")
public class UserApplyController {
    @Autowired
    private UserApplyService userApplyService;


    /**
     * 申请加为好友
     */
    @PostMapping("/apply")
    @AuthCheck(mustRole = "login")
    @ApiOperation("申请加为好友")
    public R applyFriend(@RequestBody UserApplyRequest request) {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        userApplyService.applyFriend(userId, request);
        return R.ok();
    }

    /**
     * 同意好友申请
     */
    @PostMapping("/agree")
    @ApiOperation("同意好友申请")
    @AuthCheck(mustRole = "login")
    public R agreeApply(@RequestParam("applyId") Long applyId) {
        ChatMessageVO chatMessageVO = userApplyService.agreeApply(AuthInterceptor.userThreadLocal.get().getId(), applyId);
        return R.ok(chatMessageVO);
    }

    /**
     * 查询好友申请列表（游标）
     */
    @PostMapping("/cursor/list")
    @AuthCheck(mustRole = "login")
    @ApiOperation("获取好友申请列表")
    public R getFriendApplyPageByCursor(@RequestBody CursorPageBaseRequest cursorPageBaseRequest) {
        User user = AuthInterceptor.userThreadLocal.get();
        CursorPageBaseVO<FriendApplyVO> cursorPageBaseVO = userApplyService.getPageByCursor(cursorPageBaseRequest, user.getId());
        return R.ok(cursorPageBaseVO);
    }

    /**
     * 标记好友申请为已读 以当前时间为界
     */
    @GetMapping("/read/mark")
    @AuthCheck(mustRole = "login")
    public R markReadFriendApply() {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        userApplyService.markReadFriendApply(userId, new Date());
        return R.ok();
    }


    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = userApplyService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id){
//		UserApply userApply = userApplyService.getById(id);
//
//        return R.ok().put("userApply", userApply);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody UserApply userApply){
//		userApplyService.save(userApply);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody UserApply userApply){
//		userApplyService.updateById(userApply);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//		userApplyService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
