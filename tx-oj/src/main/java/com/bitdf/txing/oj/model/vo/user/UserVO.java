package com.bitdf.txing.oj.model.vo.user;

import com.lemon.util.anno.MysqlColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 大学
     */
    private String school;
    /**
     * 专业
     */
    private String profession;
    /**
     * 工作经验
     */
    private Integer workExperience;
    /**
     * 刷题数
     */
    private Integer questionCount;
    /**
     * 通过率
     */
    private Float acceptedRate;
    /**
     * 提交数
     */
    private Integer submitCount;
    /**
     * 通过数
     */
    private Integer acceptedCount;
    /**
     * 个性签名
     */
    private String personSign;

    @ApiModelProperty(value = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;

    /**
     * 最近一次上下线时间
     */
    private Date lastOpsTime;
    /**
     * 在线状态 0:在线 1: 下线
     */
    @MysqlColumn(defaultValue = "0")
    private Integer activeStatus;


    private static final long serialVersionUID = 1L;

    public static UserVO skip(Long userId) {
        return UserVO.builder()
                .id(userId)
                .needRefresh(false)
                .build();
    }
}
