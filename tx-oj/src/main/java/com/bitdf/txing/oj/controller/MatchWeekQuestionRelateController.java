package com.bitdf.txing.oj.controller;

import com.bitdf.txing.oj.model.entity.match.WeekMatchQuestionRelate;
import com.bitdf.txing.oj.service.MatchWeekQuestionRelateService;
import com.bitdf.txing.oj.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;



/**
 * 
 *
 * @author lizhiwei
 * @email 
 * @date 2024-03-13 15:00:49
 */
@RestController
@RequestMapping("oj/matchweekquestionrelate")
public class MatchWeekQuestionRelateController {
    @Autowired
    private MatchWeekQuestionRelateService matchWeekQuestionRelateService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = matchWeekQuestionRelateService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WeekMatchQuestionRelate matchWeekQuestionRelate = matchWeekQuestionRelateService.getById(id);

        return R.ok().put("matchWeekQuestionRelate", matchWeekQuestionRelate);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WeekMatchQuestionRelate matchWeekQuestionRelate){
		matchWeekQuestionRelateService.save(matchWeekQuestionRelate);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WeekMatchQuestionRelate matchWeekQuestionRelate){
		matchWeekQuestionRelateService.updateById(matchWeekQuestionRelate);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		matchWeekQuestionRelateService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
