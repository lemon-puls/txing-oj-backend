package com.bitdf.txing.oj.model.dto.user;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;


    /**
     * 简介
     */
    private String userProfile;

    /**
     * 大学
     */
    private String school;
    /**
     * 专业
     */
    private String profession;
    /**
     * 个性签名
     */
    private String personSign;

    private static final long serialVersionUID = 1L;
}