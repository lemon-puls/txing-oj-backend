package com.bitdf.txing.oj.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/24 22:12:17
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsFriendApplyVO {
    /**
     * 申请基本信息
     */
    private FriendApplyVO friendApplyVO;
    /**
     * 未读申请总数
     */
    private Integer unreadCount;
}
