package com.bitdf.txing.oj.service.cache;

import java.util.List;
import java.util.Map;

/**
 * @author Lizhiwei
 * @date 2023/12/29 20:50:41
 * 注释：
 */
public interface BatchCache<IN, OUT> {
    /**
     * 获取单个
     */
    OUT get(IN req);

    /**
     * 获取批量
     */
    Map<IN, OUT> getBatch(List<IN> req);

    /**
     * 修改删除单个
     */
    void delete(IN req);

    /**
     * 修改删除多个
     */
    void deleteBatch(List<IN> req);
}

