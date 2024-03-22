package com.bitdf.txing.oj.controller;

import com.bitdf.txing.oj.service.MatchSubmitRelateService;
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
@RequestMapping("oj/matchsubmitrelate")
public class MatchSubmitRelateController {
    @Autowired
    private MatchSubmitRelateService matchSubmitRelateService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = matchSubmitRelateService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }


//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id){
//		MatchSubmitRelate matchSubmitRelate = matchSubmitRelateService.getById(id);
//
//        return R.ok().put("matchSubmitRelate", matchSubmitRelate);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    public R save(@RequestBody MatchSubmitRelate matchSubmitRelate){
//		matchSubmitRelateService.save(matchSubmitRelate);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    public R update(@RequestBody MatchSubmitRelate matchSubmitRelate){
//		matchSubmitRelateService.updateById(matchSubmitRelate);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//		matchSubmitRelateService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
