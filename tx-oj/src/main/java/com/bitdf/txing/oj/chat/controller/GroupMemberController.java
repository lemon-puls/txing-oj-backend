package com.bitdf.txing.oj.chat.controller;

import com.bitdf.txing.oj.chat.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author lizhiwei
 * @email
 * @date 2023-12-28 10:48:15
 */
@RestController
@RequestMapping("chat/group/member")
public class GroupMemberController {
    @Autowired
    private GroupMemberService groupMemberService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params) {
////        PageUtils page = groupMemberService.queryPage(params);
////
////        return R.ok().put("page", page);
//        return R.ok();
//    }


    /**
     * 信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id) {
//        GroupMember groupMember = groupMemberService.getById(id);
//
//        return R.ok().put("groupMember", groupMember);
//    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody GroupMember groupMember) {
//        groupMemberService.save(groupMember);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    public R update(@RequestBody GroupMember groupMember) {
//        groupMemberService.updateById(groupMember);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids) {
//        groupMemberService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
