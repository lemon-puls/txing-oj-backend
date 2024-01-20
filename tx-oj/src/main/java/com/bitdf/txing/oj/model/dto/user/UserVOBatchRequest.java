package com.bitdf.txing.oj.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2024/1/14 15:33:00
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVOBatchRequest {
    private List<ItemRequest> requestList;

    @Data
    public static class ItemRequest {
        private Long userId;
        private Long lastModifyTime;
    }
}
