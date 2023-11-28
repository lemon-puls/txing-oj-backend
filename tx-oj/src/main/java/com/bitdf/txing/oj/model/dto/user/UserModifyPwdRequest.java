package com.bitdf.txing.oj.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author Lizhiwei
 * @date 2023/11/19 23:28:29
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyPwdRequest {
    @NotBlank
    @Length(min = 8)
    private String oldPassword;
    @NotBlank
    @Length(min = 8)
    private String userPassword;
    @NotBlank
    @Length(min = 8)
    private String checkPassword;
}
