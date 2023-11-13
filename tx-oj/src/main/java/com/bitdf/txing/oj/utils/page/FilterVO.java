package com.bitdf.txing.oj.utils.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/9/8 13:47:35
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterVO {
    public static final String eq = "eq";
    public static final String like = "like";
    public static final String le = "le";
    public static final String ge = "ge";
    public static final String between = "between";
    public static final String in = "in";
    public static final String notIn = "notIn";
    public static final String ne = "ne";

    /**
     * 字段名
     */
    private String fieldName;
    /**
     * value
     */
    private String value;
    /**
     * 查询方式
     */
    private String queryType;

}
