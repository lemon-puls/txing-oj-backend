package com.bitdf.txing.oj.chat.controller;

import com.bitdf.txing.oj.chat.service.RoomFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 *
 *
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@RequestMapping("chat/friend")
public class RoomFriendController {
    @Autowired
    private RoomFriendService friendService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
////        PageUtils page = friendService.queryPage(params);
////
////        return R.ok().put("page", page);
//        return R.ok();
//    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id){
//		RoomFriend friend = friendService.getById(id);
//
//        return R.ok().put("friend", friend);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody RoomFriend friend){
//		friendService.save(friend);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody RoomFriend friend){
//		friendService.updateById(friend);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//		friendService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
