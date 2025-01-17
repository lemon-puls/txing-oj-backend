package com.bitdf.txing.oj.model.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@TableName(value = "tx_oj_user")
@Data
public class User implements Serializable {

    /**
     * 系统id
     */
    public static final Long SYSTEM_USER_ID = 0L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;

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
     * 个性签名
     */
    private String personSign;

    /**
     * 在线状态 0:在线 1: 下线
     */
    private Integer activeStatus;


    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 提交数
     */
    private Integer submitCount;
    /**
     * 通过数
     */
    private Integer acceptedCount;

    /**
     * 最近一次上下线时间
     */
    private Date lastOpsTime;
    /**
     * 竞赛积分
     */
    private Integer matchScore;

    private Integer status;

    private String remark;
}
