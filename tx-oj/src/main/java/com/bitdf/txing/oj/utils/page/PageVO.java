package com.bitdf.txing.oj.utils.page;

import com.bitdf.txing.oj.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/9/7 11:03:23
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVO {
    /**
     * 过滤参数
     */
    private List<FilterVO> filter;
    /**
     * 分页参数
     */
    private Page page;
    /**
     * 排序数组
     */
    private Sort[] sorts;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Page {
        /**
         * 当前页号
         */
        private Integer current = 1;

        /**
         * 页面大小
         */
        private Integer pageSize = 10;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sort {
        /**
         * 排序字段名
         */
        String sortName;
        /**
         * 是否升序
         */
        Boolean isAsc;
    }

}
