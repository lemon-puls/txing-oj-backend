package com.bitdf.txing.oj.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lizhiwei
 * @date 2024/1/15 11:20:28
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendApplyVO {
    /**
     * id
     */
    private Long id;
    /**
     * 申请人id
     */
    private Long userId;
    /**
     * 申请类型 0：加好友
     */
    private Integer type;
    /**
     * 申请内容
     */
    private String msg;
    /**
     * 申请状态：0：待通过 1：已通过
     */
    private Integer status;
    private Date createTime;
    /**
     * 阅读状态：0：未读 1：已读
     */
    private Integer readStatus;


}
