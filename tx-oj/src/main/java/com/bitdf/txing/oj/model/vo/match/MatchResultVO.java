package com.bitdf.txing.oj.model.vo.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lizhiwei
 * @date 2024/4/14 18:48
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchResultVO {

    List<ResultItem> resultItems;

    Integer acCount;

    Long seconds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultItem {
        String label;

        String value;
    }
}


