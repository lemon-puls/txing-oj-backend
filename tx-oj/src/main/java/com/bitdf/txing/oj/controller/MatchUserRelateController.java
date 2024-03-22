package com.bitdf.txing.oj.controller;

import com.bitdf.txing.oj.service.MatchUserRelateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2024-03-13 15:00:49
 */
@RestController
@RequestMapping("oj/matchuserrelate")
public class MatchUserRelateController {
    @Autowired
    private MatchUserRelateService matchUserRelateService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = matchUserRelateService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }


//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id){
//		MatchUserRelate matchUserRelate = matchUserRelateService.getById(id);
//
//        return R.ok().put("matchUserRelate", matchUserRelate);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    public R save(@RequestBody MatchUserRelate matchUserRelate){
//		matchUserRelateService.save(matchUserRelate);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    public R update(@RequestBody MatchUserRelate matchUserRelate){
//		matchUserRelateService.updateById(matchUserRelate);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//		matchUserRelateService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
