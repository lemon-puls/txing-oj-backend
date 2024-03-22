package com.bitdf.txing.oj.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import org.springframework.beans.BeanUtils;

public class UserAdapter {

    public static UserShowVO buildUserShowVO(User user) {
        UserShowVO userShowVO = new UserShowVO();
        BeanUtils.copyProperties(user, userShowVO);
        return userShowVO;
    }
}
