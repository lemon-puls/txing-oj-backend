package com.bitdf.txing.oj.chat.controller;

import java.util.Arrays;
import java.util.Map;

import com.bitdf.txing.oj.model.entity.chat.Room;
import com.bitdf.txing.oj.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bitdf.txing.oj.chat.service.RoomService;


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
