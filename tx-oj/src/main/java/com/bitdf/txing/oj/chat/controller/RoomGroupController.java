package com.bitdf.txing.oj.chat.controller;

import com.bitdf.txing.oj.chat.service.RoomGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@RequestMapping("chat/group")
public class RoomGroupController {
    @Autowired
    private RoomGroupService groupService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params) {
////        PageUtils page = groupService.queryPage(params);
////
////        return R.ok().put("page", page);
//        return R.ok();
//    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id) {
//        RoomGroup group = groupService.getById(id);
//
//        return R.ok().put("group", group);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody RoomGroup group) {
//        groupService.save(group);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody RoomGroup group) {
//        groupService.updateById(group);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids) {
//        groupService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
