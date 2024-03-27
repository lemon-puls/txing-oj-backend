package com.bitdf.txing.oj.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShowVO {

    private Long id;

    private String userName;

    private String userAvatar;

    private String personSign;
}
