package com.bitdf.txing.oj.controller;

import com.bitdf.txing.oj.service.MatchWeekService;
import com.bitdf.txing.oj.service.business.MatchAppService;
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
@RequestMapping("match/week")
public class MatchWeekController {
    @Autowired
    private MatchWeekService matchWeekService;
    @Autowired
    MatchAppService matchAppService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = matchWeekService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }

//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id){
//		WeekMatch matchWeek = matchWeekService.getById(id);
//
//        return R.ok().put("matchWeek", matchWeek);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    public R save(@RequestBody WeekMatch matchWeek){
//		matchWeekService.save(matchWeek);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    public R update(@RequestBody WeekMatch matchWeek){
//		matchWeekService.updateById(matchWeek);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//		matchWeekService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
