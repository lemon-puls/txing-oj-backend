package com.bitdf.txing.oj.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.service.UserFriendService;



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
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = userFriendService.queryPage(params);
//
//        return R.ok().put("page", page);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		UserFriend userFriend = userFriendService.getById(id);

        return R.ok().put("userFriend", userFriend);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UserFriend userFriend){
		userFriendService.save(userFriend);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UserFriend userFriend){
		userFriendService.updateById(userFriend);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		userFriendService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


}
