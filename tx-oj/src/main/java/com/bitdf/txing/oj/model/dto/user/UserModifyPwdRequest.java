package com.bitdf.txing.oj.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2023/11/19 23:28:29
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyPwdRequest {

    private String oldPassword;

    private String userPassword;

    private String checkPassword;
}
