package com.bitdf.txing.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.common.DeleteRequest;
import com.bitdf.txing.oj.constant.UserConstant;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.model.dto.user.*;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.match.WeekMatchRankItemVO;
import com.bitdf.txing.oj.model.vo.user.LoginUserVO;
import com.bitdf.txing.oj.model.vo.user.UserVO;
import com.bitdf.txing.oj.service.LoginService;
import com.bitdf.txing.oj.service.QuestionSubmitService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Autowired
    QuestionSubmitService questionSubmitService;
    @Autowired
    LoginService loginService;


    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public R userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return R.error(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return R.ok(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
//        String token = UserTokenUtils.generateAndSaveUserToken(loginUserVO.getId());
//        loginUserVO.setToken(token);
        return R.ok(loginUserVO);
    }

    /**
     * 更新密码
     *
     * @param userModifyPwdRequest
     * @return
     */
    @PostMapping("/pwd/modify")
    @AuthCheck(mustRole = "login")
    public R modifyPwd(@Validated @RequestBody UserModifyPwdRequest userModifyPwdRequest, BindingResult result, HttpServletRequest request) {
        User user = AuthInterceptor.userThreadLocal.get();
        // 判断校验是否成功
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return R.error(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION).put("data", errors);
        }
        // 修改密码
        boolean modifyResult = userService.modifyPwd(userModifyPwdRequest);
        ThrowUtils.throwIf(!modifyResult, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        // 退出登录
        boolean b = loginService.userLogout(user.getId());
        return R.ok("修改密码成功 请重新登录！");
    }


    /**
     * 用户登录（微信开放平台）
     */
//    @GetMapping("/login/wx_open")
//    public R userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
//            @RequestParam("code") String code) {
//        WxOAuth2AccessToken accessToken;
//        try {
//            WxMpService wxService = wxOpenConfig.getWxMpService();
//            accessToken = wxService.getOAuth2Service().getAccessToken(code);
//            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
//            String unionId = userInfo.getUnionId();
//            String mpOpenId = userInfo.getOpenid();
//            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
//                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION, "登录失败，系统错误");
//            }
//            return R.ok(userService.userLoginByMpOpen(userInfo, request));
//        } catch (Exception e) {
//            log.error("userLoginByWxOpen error", e);
//            throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION, "登录失败，系统错误");
//        }
//    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    @AuthCheck(mustRole = "login")
    public R userLogout() {
        Long userId = AuthInterceptor.userThreadLocal.get().getId();
        boolean result = loginService.userLogout(userId);
        return R.ok(result);
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    @GetMapping("/get/login")
    @AuthCheck(mustRole = "login")
    public R getLoginUser() {
        User user = AuthInterceptor.userThreadLocal.get();
        return R.ok(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        return R.ok(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return R.ok(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                        HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        return R.ok(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, TxCodeEnume.COMMON_TARGET_NOT_EXIST_EXCEPTION);
        return R.ok(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public R getUserVOById(long id, HttpServletRequest request) {
        R response = getUserById(id, request);
        User user = (User) response.get("data");
        return R.ok(userService.getUserVO(user));
    }

    /**
     * 获取当前用户VO
     *
     * @return
     */
    @AuthCheck(mustRole = "login")
    @GetMapping("/current/get/vo")
    public R getCurrentUserVOById() {
        User user = AuthInterceptor.userThreadLocal.get();
        user = userService.getById(user.getId());
        return R.ok(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                            HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return R.ok(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public R listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                              HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return R.ok(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    @AuthCheck(mustRole = "login")
    public R updateMyUser(@Validated @RequestBody UserUpdateMyRequest userUpdateMyRequest,
                          BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return R.error(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION).put("data", errors);
        }
        if (userUpdateMyRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        User loginUser = AuthInterceptor.userThreadLocal.get();
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, TxCodeEnume.COMMON_OPS_FAILURE_EXCEPTION);
        return R.ok(true);
    }

//    @PostMapping("/avatar/update")
//    @AuthCheck(mustRole = "login")
//    public R updateAvatar() {
//
//    }

    @PostMapping("/vo/batch/get")
    public R getUserVoBatch(@RequestBody UserVOBatchRequest request) {
        List<UserVO> userVOS = userService.getUserVOBatch(request);
        return R.ok(userVOS);
    }

    /**
     * 查询用户竞赛积分排名
     */
    @GetMapping("/match/score/rank")
    public R getUserScoreRank() {
        List<WeekMatchRankItemVO> ranks = userService.getUserScoreRank();
        return R.ok(ranks);
    }
}
